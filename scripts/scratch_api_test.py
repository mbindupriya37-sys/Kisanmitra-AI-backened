import json
import os
import urllib.error
import urllib.request

BASE_URL = "https://kisanmitra-ai-backened.onrender.com"
TEST_PASSWORD = os.environ.get("TEST_ACCOUNT_PASSWORD")


def post_json(url, data):
    req_data = json.dumps(data).encode("utf-8")
    headers = {"Content-Type": "application/json"}
    req = urllib.request.Request(url, data=req_data, headers=headers, method="POST")
    try:
        with urllib.request.urlopen(req) as resp:
            return resp.getcode(), json.loads(resp.read().decode("utf-8"))
    except urllib.error.HTTPError as error:
        body = error.read().decode("utf-8")
        try:
            return error.code, json.loads(body)
        except Exception:
            return error.code, body
    except Exception as error:
        return None, str(error)


def get_json(url, token=None):
    headers = {}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    req = urllib.request.Request(url, headers=headers, method="GET")
    try:
        with urllib.request.urlopen(req) as resp:
            return resp.getcode(), json.loads(resp.read().decode("utf-8"))
    except urllib.error.HTTPError as error:
        body = error.read().decode("utf-8")
        try:
            return error.code, json.loads(body)
        except Exception:
            return error.code, body
    except Exception as error:
        return None, str(error)


if not TEST_PASSWORD:
    raise SystemExit("Set TEST_ACCOUNT_PASSWORD to the password for the test accounts.")

print("=================================================================")
print("1. ROLE AUTHORIZATION MATRIX TEST FOR /api/farmer/supply-demand/overview")
print("=================================================================")

# 1. Unauthenticated / Anonymous
status_anon, body_anon = get_json(f"{BASE_URL}/api/farmer/supply-demand/overview")
print(f"ANONYMOUS Request -> HTTP Status: {status_anon} (Expected 401 or 403)")
assert status_anon in [401, 403], f"Anonymous request should be rejected, got {status_anon}"

# 2. Login EXPERT
status_exp_login, body_exp_login = post_json(f"{BASE_URL}/api/auth/login", {
    "email": "expert@example.com",
    "password": TEST_PASSWORD
})
assert status_exp_login == 200, f"Expert login failed: {body_exp_login}"
expert_token = body_exp_login.get("token")
status_exp_req, body_exp_req = get_json(f"{BASE_URL}/api/farmer/supply-demand/overview", token=expert_token)
print(f"EXPERT Role Request -> HTTP Status: {status_exp_req} (Expected 403)")
assert status_exp_req == 403, f"Expert request should be 403 Forbidden, got {status_exp_req}"

# 3. Login ADMIN
status_adm_login, body_adm_login = post_json(f"{BASE_URL}/api/auth/login", {
    "email": "admin@example.com",
    "password": TEST_PASSWORD
})
assert status_adm_login == 200, f"Admin login failed: {body_adm_login}"
admin_token = body_adm_login.get("token")
status_adm_req, body_adm_req = get_json(f"{BASE_URL}/api/farmer/supply-demand/overview", token=admin_token)
print(f"ADMIN Role Request -> HTTP Status: {status_adm_req} (Expected 403)")
assert status_adm_req == 403, f"Admin request should be 403 Forbidden, got {status_adm_req}"

# 4. Login FARMER
status_far_login, body_far_login = post_json(f"{BASE_URL}/api/auth/login", {
    "email": "farmer@example.com",
    "password": TEST_PASSWORD
})
assert status_far_login == 200, f"Farmer login failed: {body_far_login}"
farmer_token = body_far_login.get("token")
status_far_req, body_far_req = get_json(f"{BASE_URL}/api/farmer/supply-demand/overview", token=farmer_token)
print(f"FARMER Role Request -> HTTP Status: {status_far_req} (Expected 200)")
assert status_far_req == 200, f"Farmer request should be 200 OK, got {status_far_req}"

print("\n=================================================================")
print("2. FILTER COMBINATIONS & VERIFICATION CHECKS (AUTHENTICATED FARMER)")
print("=================================================================")

# Test A: Standard Rice / AP / Krishna / Kharif
url_a = f"{BASE_URL}/api/farmer/supply-demand/overview?crop=Rice&state=Andhra+Pradesh&district=Krishna&season=Kharif"
status_a, res_a = get_json(url_a, token=farmer_token)
print(f"Test A (Rice, AP, Krishna, Kharif) -> Status: {status_a}")
assert status_a == 200

demand_balance = res_a.get("regionalDemandBalance", {})
assert demand_balance.get("demandStatus") == "INSUFFICIENT_DATA", "Demand status must be INSUFFICIENT_DATA"
assert demand_balance.get("isCalculated") is False, "Demand isCalculated must be False"
assert demand_balance.get("surplusDeficitRatio") is None, "surplusDeficitRatio must be None"
assert demand_balance.get("surplusDeficitQuantity") is None, "surplusDeficitQuantity must be None"
print("  [PASS] Demand status strictly INSUFFICIENT_DATA (Zero fabrication)")

hist_prod = res_a.get("historicalProduction", {})
assert len(hist_prod.get("observations", [])) == 0, "Historical observations must be 0"
assert hist_prod.get("fiveYearAverageProductionTonnes") is None, "5-yr avg must be None when no data"
print("  [PASS] Historical production table is empty, 5-yr avg is None")

msp_notif = res_a.get("mspNotification", {})
assert msp_notif.get("status") == "MISSING_NOTIFICATION", f"MSP status expected MISSING_NOTIFICATION, got {msp_notif.get('status')}"
assert msp_notif.get("effectiveMspInrPerQuintal") is None, "Effective MSP must be None"
print("  [PASS] MSP status MISSING_NOTIFICATION, no fabricated MSP rate")

buf_stock = res_a.get("bufferStocks", {})
assert buf_stock.get("commodity") == "RICE", "Buffer commodity resolved to RICE"
assert buf_stock.get("actualCentralPoolLmt") is None, "Buffer stock actual must be None"
print("  [PASS] Buffer stocks empty state verified for Rice")

mandi = res_a.get("mandiPrices", {})
assert mandi.get("availabilityStatus") == "UNCONFIRMED_OR_OFFLINE", "Mandi status must be UNCONFIRMED_OR_OFFLINE"
assert len(mandi.get("observations", [])) == 0, "Mandi observations must be empty"
print("  [PASS] Mandi status UNCONFIRMED_OR_OFFLINE, observations empty without fake prices")

# Test B: Cotton / Maharashtra / Nagpur / Kharif (Fiber crop check)
url_b = f"{BASE_URL}/api/farmer/supply-demand/overview?crop=Cotton&state=Maharashtra&district=Nagpur&season=Kharif"
status_b, res_b = get_json(url_b, token=farmer_token)
print(f"\nTest B (Cotton, Maharashtra, Nagpur, Kharif) -> Status: {status_b}")
assert status_b == 200
buf_cotton = res_b.get("bufferStocks", {})
assert buf_cotton.get("commodity") is None, "Cotton should not have buffer commodity"
assert buf_cotton.get("comparisonStatus") == "NOT_APPLICABLE", "Cotton buffer stock comparisonStatus must be NOT_APPLICABLE"
print("  [PASS] Cotton correctly identifies Buffer Stocks NOT_APPLICABLE")

# Test C: Unknown / Unregistered Crop
url_c = f"{BASE_URL}/api/farmer/supply-demand/overview?crop=UnknownFruitCrop&state=Punjab&district=Ludhiana&season=Rabi"
status_c, res_c = get_json(url_c, token=farmer_token)
print(f"\nTest C (UnknownFruitCrop, Punjab, Ludhiana, Rabi) -> Status: {status_c}")
assert status_c == 200
msp_c = res_c.get("mspNotification", {})
assert msp_c.get("status") == "UNRESOLVED_MAPPING", f"Expected UNRESOLVED_MAPPING, got {msp_c.get('status')}"
print("  [PASS] Unknown crop returns UNRESOLVED_MAPPING gracefully")

# Test D: Default parameters (No query params)
url_d = f"{BASE_URL}/api/farmer/supply-demand/overview"
status_d, res_d = get_json(url_d, token=farmer_token)
print(f"\nTest D (Default parameters) -> Status: {status_d}")
assert status_d == 200
assert res_d.get("queryCrop") == "Rice"
assert res_d.get("queryState") == "Andhra Pradesh"
assert res_d.get("queryDistrict") == "Krishna"
print("  [PASS] Default parameters fallback properly")

print("\n>>> ALL API AUTHORIZATION, FILTER AND DATA INTEGRITY CHECKS PASSED SUCCESSFULLY! <<<")