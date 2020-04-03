package com.noqapp.android.common.model.types.category;

/**
 * hitender
 * 04/01/20 3:33 PM
 */
public enum GroceryEnum {
    BNH("BNH", "Beauty & Health"),
    COS("COS", "Cosmetics"),
    HOL("HOL", "Hair Oil"),
    HED("HED", "Henna - Dye"),
    MED("MED", "Medicines"),
    SMP("SMP", "Shampoo"),
    SOP("SOP", "Soaps - Pastes"),
    BEV("BEV", "Beverages"),
    COF("COF", "Coffee"),
    DNK("DNK", "Drinks"),
    HNK("HNK", "Health Drinks"),
    JUC("JUC", "Juice Mixes"),
    TEA("TEA", "Tea"),
    CNF("CNF", "Confectioneries"),
    BCK("BCK", "Biscuits - Cakes"),
    CDY("CDY", "Candy"),
    RUS("RUS", "Cookies - Rusks"),
    DES("DES", "Desserts - Sweets"),
    SWT("SWT", "Fresh Sweets"),
    JAM("JAM", "Jams - Jello"),
    COE("COE", "Cooking Essentials"),
    CAV("CAV", "Canned Vegetables"),
    COO("COO", "Cooking Oils"),
    COP("COP", "Cooking Pastes"),
    LOS("LOS", "Loose Spices"),
    SAF("SAF", "Saffron"),
    SPM("SPM", "Spice Mixes"),
    EMS("EMS", "E-Miscellaneous"),
    APP("APP", "Appliances"),
    COL("COL", "Colors - Essences"),
    CUS("CUS", "Custard - Sugar"),
    GFT("GFT", "Gifts"),
    INC("INC", "Incenses"),
    POI("POI", "Pooja Items"),
    FFO("FFO", "Frozen Foods"),
    BRD("BRD", "Breads"),
    DAI("DAI", "Dairy Products"),
    ENT("ENT", "Entrees - Dinners"),
    ICE("ICE", "Ice-Creams"),
    NVG("NVG", "Non-Vegetarian"),
    NOS("NOS", "North Snacks"),
    SOS("SOS", "South Snacks"),
    VEG("VEG", "Vegetables"),
    GFO("GFO", "GOURMET FOOD"),
    GCO("GCO", "Gourmet Cookies"),
    GKI("GKI", "Gourmet Kitchen"),
    GOA("GOA", "Gourmet Oats"),
    GPA("GPA", "Gourmet Papad"),
    GPI("GPI", "Gourmet Pickles"),
    GPW("GPW", "Gourmet Powders"),
    GSN("GSN", "Gourmet Snacks"),
    GSP("GSP", "Gourmet Spices"),
    GSW("GSW", "Gourmet Sweets"),
    GRO("GRO", "Groceries"),
    FIS("FIS", "Fish - Meat"),
    FRV("FRV", "Fresh Vegetables"),
    GHE("GHE", "Ghee"),
    JAG("JAG", "Jaggery"),
    MUK("MUK", "Mukhwas"),
    PAP("PAP", "Papad"),
    POL("POL", "Poultry"),
    TAM("TAM", "Tamarind"),
    VER("VER", "Vermicelli"),
    INS("INS", "Instant Food"),
    CUT("CUT", "Chutneys - Sauces"),
    FRS("FRS", "Fresh Snacks"),
    INF("INF", "Infant Food"),
    INM("INM", "Instant Mixes"),
    NOO("NOO", "Noodles"),
    PIK("PIK", "Pickles"),
    RTE("RTE", "Ready To Eat"),
    SNK("SNK", "Snacks"),
    SOU("SOU", "Soups"),
    STA("STA", "Staples"),
    ATT("ATT", "Atta"),
    BES("BES", "Besan"),
    DAL("DAL", "Dals/Lentils"),
    FLO("FLO", "Flours"),
    MAM("MAM", "Mamra - Poha"),
    NUT("NUT", "Nuts - Dry Fruits"),
    RIC("RIC", "Rice"),
    SAB("SAB", "Sabudana"),
    SOY("SOY", "Soya-Vadi"),
    OTH("OTH", "Other - Miscellaneous");

    private final String description;
    private final String name;

    GroceryEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
