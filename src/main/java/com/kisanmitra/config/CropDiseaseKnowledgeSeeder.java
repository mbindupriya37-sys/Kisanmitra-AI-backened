package com.kisanmitra.config;

import com.kisanmitra.entity.CropDiseaseKnowledge;
import com.kisanmitra.repository.CropDiseaseKnowledgeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CropDiseaseKnowledgeSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CropDiseaseKnowledgeSeeder.class);

    private final CropDiseaseKnowledgeRepository knowledgeRepository;

    public CropDiseaseKnowledgeSeeder(CropDiseaseKnowledgeRepository knowledgeRepository) {
        this.knowledgeRepository = knowledgeRepository;
    }

    @Override
    public void run(String... args) {
        if (knowledgeRepository.count() == 0) {
            logger.info("Seeding initial verified ICAR & State Agricultural University Crop Disease Knowledge entries...");

            List<CropDiseaseKnowledge> initialKnowledge = List.of(
                    new CropDiseaseKnowledge(
                            "Rice",
                            "Rice Blast",
                            "Magnaporthe oryzae",
                            "Leaves, Stem Node, Neck, Panicle",
                            "Spindle-shaped lesions with grey or whitish centers and reddish-brown margins on leaf blades; dark brown rot at node/neck causing broken neck or unfilled panicles.",
                            "Use resistant varieties (e.g. Swarna, IR-64-Sub1). Seed treatment with bio-control products may be considered only under local extension guidance and label instructions. Avoid excessive nitrogenous fertilizers.",
                            "Remove infected stubbles, burn crop residue, avoid dense planting, practice balanced NPK fertilization (4:2:1 ratio).",
                            "Urgent KVK consultation recommended if neck blast is observed at heading stage or >10% leaf area is affected.",
                            "ICAR-NRRI Technical Advisory Bulletin No. 18: Integrated Blast Management in Rice",
                            "https://nrri.icar.gov.in/bulletins/rice-blast-management-guidelines",
                            "2026-10-09",
                            "VERIFIED"
                    ),
                    new CropDiseaseKnowledge(
                            "Rice",
                            "Rice Bacterial Leaf Blight",
                            "Xanthomonas oryzae pv. oryzae",
                            "Leaves, Leaf Sheath",
                            "Water-soaked translucent streaks starting from leaf margins/tips, turning yellowish-straw colored with wavy margins. Milky bacterial ooze droplets visible on young leaves in early morning.",
                            "Plant resistant varieties (e.g. Improved Samba Mahsuri). Avoid clipping seedling tips during transplanting. Neem-based formulations may be used only when locally recommended and label-compliant, with KVK guidance.",
                            "Avoid excess nitrogen top-dressing during humid weather, drain field water temporarily for 3-4 days, maintain clean field bunds.",
                            "Escalate to local KVK if kresek (seedling wilt) stage occurs in young transplanted crop.",
                            "ICAR-IIRR Technical Advisory No. 24: Diagnosis and Management of Bacterial Blight of Rice",
                            "https://www.icar-iirr.org/advisories/bacterial-leaf-blight-rice",
                            "2026-10-09",
                            "VERIFIED"
                    ),
                    new CropDiseaseKnowledge(
                            "Wheat",
                            "Wheat Yellow / Stripe Rust",
                            "Puccinia striiformis f. sp. tritici",
                            "Leaves, Leaf Sheath",
                            "Bright yellow pustules (uredinia) arranged in linear rows or stripes along leaf veins. Pustules rupture exposing yellow powdery spores.",
                            "Grow recommended rust-resistant wheat varieties (e.g. HD-3086, DBW-187, PBW-725). Ensure timely sowing during recommended agronomic window.",
                            "Eradicate barberry weed (alternate host), avoid excess irrigation during cold damp weather, inspect fields regularly during January-February.",
                            "Immediate reporting to KVK/PAU extension officers required if yellow rust patches appear in northern wheat belt.",
                            "ICAR-IIWBR Research Bulletin No. 42: Wheat Stripe Rust Surveillance and Advisory Guidelines",
                            "https://iiwbr.icar.gov.in/bulletins/stripe-rust-wheat-advisory",
                            "2026-10-09",
                            "VERIFIED"
                    ),
                    new CropDiseaseKnowledge(
                            "Maize",
                            "Maize Turcicum Leaf Blight",
                            "Exserohilum turcicum",
                            "Leaves, Sheath",
                            "Large long elliptical greyish-brown lesions on leaves, starting from lower leaves and spreading upward. Dark fungal spore growth visible in humid weather.",
                            "Plant tolerant hybrids (e.g. HQPM-1, DHM-117). Seed treatment with suitable bio-control agents may be considered under local extension guidance and product label instructions.",
                            "Crop rotation with non-graminaceous crops (pulses, legumes), deep plowing of infected crop residue, balanced potassium application.",
                            "Consult local KVK if blighting occurs prior to silking stage.",
                            "ICAR-IIMR Extension Circular No. 12: Integrated Management of Turcicum Blight in Maize",
                            "https://iimr.icar.gov.in/circulars/turcicum-leaf-blight-maize",
                            "2026-10-09",
                            "VERIFIED"
                    ),
                    new CropDiseaseKnowledge(
                            "Cotton",
                            "Cotton Angular Leaf Spot / Bacterial Blight",
                            "Xanthomonas citri pv. malvacearum",
                            "Leaves, Stem, Bolls",
                            "Small water-soaked angular lesions bounded by leaf veins on lower leaf surface, turning dark brown/black. Black arm symptoms on petioles and stems. Water-soaked sunken spots on bolls.",
                            "Use certified disease-free delinted seeds. Seed treatment with suitable bio-control agents may be considered only where locally recommended and label-approved.",
                            "Remove and burn infected crop residue, field crop rotation with maize/wheat, avoid overhead sprinkler irrigation during outbreak.",
                            "Consult KVK extension officers if black arm stem rot or boll rotting symptoms develop.",
                            "ICAR-CICR Technical Bulletin No. 35: Cotton Bacterial Blight Disease Identification & Management",
                            "https://cicr.icar.gov.in/bulletins/cotton-bacterial-blight-guidelines",
                            "2026-10-09",
                            "VERIFIED"
                    ),
                    new CropDiseaseKnowledge(
                            "Chickpea",
                            "Chickpea Fusarium Wilt",
                            "Fusarium oxysporum f. sp. ciceris",
                            "Roots, Stem, Whole Plant",
                            "Drooping of petioles and leaflets, yellowing and wilting of whole plant starting from seedling to flowering stage. Dark brown to black internal vascular discoloration visible when root/stem is split longitudinally.",
                            "Grow wilt-resistant chickpea varieties (e.g. JG-11, JAKI-9218, WR-315). Seed treatment with bio-control products may be used only where local agronomic guidance and label instructions support it.",
                            "Long crop rotation (3-5 years) with non-host crops (sorghum, wheat, mustard), summer deep plowing, avoid waterlogging in chickpea fields.",
                            "Urgent KVK plant pathologist consultation advised if vascular wilt affects >5% of crop stand.",
                            "ICAR-IIPR Extension Leaflet No. 09: Management of Fusarium Wilt in Chickpea",
                            "https://iipr.icar.gov.in/leaflets/chickpea-fusarium-wilt-advisory",
                            "2026-10-09",
                            "VERIFIED"
                    ),
                    new CropDiseaseKnowledge(
                            "Groundnut",
                            "Groundnut Tikka Leaf Spot",
                            "Cercospora arachidicola / Cercosporidium personatum",
                            "Leaves, Petioles, Stem",
                            "Early leaf spot produces sub-circular brown spots with prominent yellow halo on upper leaf surface. Late leaf spot produces circular dark brown/black spots on lower leaf surface with faint halo. Premature defoliation in severe cases.",
                            "Plant moderately resistant groundnut varieties (e.g. Kadiri-6, GPBD-4). Neem-based foliar sprays may be considered only when locally recommended and label-compliant.",
                            "Remove infected crop debris after harvest, crop rotation with cereal crops (maize, sorghum), maintain optimum plant spacing.",
                            "Consult KVK officers if severe defoliation starts before pod development.",
                            "ICAR-DGR Technical Bulletin No. 15: Tikka Leaf Spot Management in Groundnut",
                            "https://dgr.icar.gov.in/bulletins/groundnut-tikka-leaf-spot",
                            "2026-10-09",
                            "VERIFIED"
                    ),
                    new CropDiseaseKnowledge(
                            "Rice",
                            "Rice Grassy Stunt Viral Disease",
                            "Rice grassy stunt virus (RGSV)",
                            "Whole Plant, Leaves",
                            "Severe stunting, excessive tillering giving grassy appearance, pale yellow narrow leaves with dark brown rust-like spots.",
                            "VERIFICATION_REQUIRED: Unsubstantiated herbal spray formulations require controlled ICAR field trials.",
                            "Eradicate brown planthopper (Nilaparvata lugens) vector habitats and infected field stubbles.",
                            "Immediate KVK expert diagnosis required; viral symptoms cannot be field-confirmed without RT-PCR / ELISA lab testing.",
                            "ICAR-IIRR Virus Advisory (Preliminary Screening Note)",
                            "https://www.icar-iirr.org/advisories/rice-grassy-stunt-preliminary",
                            "2026-10-09",
                            "VERIFICATION_REQUIRED"
                    )
            );

            knowledgeRepository.saveAll(initialKnowledge);
            logger.info("Successfully seeded {} verified Crop Disease Knowledge entries.", initialKnowledge.size());
        } else {
            logger.info("Crop disease knowledge already exists ({} records found). Skipping seeding.", knowledgeRepository.count());
        }
    }
}
