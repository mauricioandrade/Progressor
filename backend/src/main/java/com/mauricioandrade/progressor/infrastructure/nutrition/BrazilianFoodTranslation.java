package com.mauricioandrade.progressor.infrastructure.nutrition;

import java.util.List;
import java.util.Map;
import static java.util.Map.entry;

/**
 * Bidirectional PT ↔ EN food name translation for the USDA FoodData Central integration.
 *
 * <p><b>PT → EN (query direction):</b> converts Portuguese search terms to English before
 * hitting the USDA API. Multi-word entries (e.g. "batata doce") are listed first so they
 * match before their single-word components ("batata").
 *
 * <p><b>EN → PT (result direction):</b> converts USDA English food descriptions to
 * readable Portuguese display names. Keyword matching is ordered longest-first for the
 * same reason. Cooking-method suffixes (cru, cozido, grelhado…) are appended when
 * detected in the original USDA description.
 */
public final class BrazilianFoodTranslation {

    private BrazilianFoodTranslation() {}

    // ── PT → EN — longer/more-specific phrases first ─────────────────────────
    private static final List<Map.Entry<String, String>> PT_TO_EN = List.of(
        entry("batata doce",        "sweet potato"),
        entry("carne moída",        "ground beef"),
        entry("carne moida",        "ground beef"),
        entry("peito de frango",    "chicken breast"),
        entry("coxa de frango",     "chicken thigh"),
        entry("filé mignon",        "beef tenderloin"),
        entry("file mignon",        "beef tenderloin"),
        entry("peito de peru",      "turkey breast"),
        entry("pasta de amendoim",  "peanut butter"),
        entry("grão de bico",       "chickpeas"),
        entry("grao de bico",       "chickpeas"),
        entry("arroz integral",     "brown rice"),
        entry("arroz branco",       "white rice"),
        entry("feijão preto",       "black beans"),
        entry("feijao preto",       "black beans"),
        entry("feijão carioca",     "pinto beans"),
        entry("feijao carioca",     "pinto beans"),
        entry("leite desnatado",    "skim milk"),
        entry("leite integral",     "whole milk"),
        entry("frango",             "chicken"),
        entry("arroz",              "rice"),
        entry("feijão",             "beans"),
        entry("feijao",             "beans"),
        entry("ovo",                "egg"),
        entry("ovos",               "eggs"),
        entry("patinho",            "ground beef"),
        entry("alcatra",            "beef sirloin"),
        entry("maminha",            "beef sirloin"),
        entry("picanha",            "beef rump cap"),
        entry("carne",              "beef"),
        entry("salmão",             "salmon"),
        entry("salmao",             "salmon"),
        entry("atum",               "tuna"),
        entry("tilápia",            "tilapia"),
        entry("tilapia",            "tilapia"),
        entry("peixe",              "fish"),
        entry("batata",             "potato"),
        entry("mandioca",           "cassava"),
        entry("aipim",              "cassava"),
        entry("macaxeira",          "cassava"),
        entry("aveia",              "oats"),
        entry("pão",                "bread"),
        entry("pao",                "bread"),
        entry("leite",              "milk"),
        entry("queijo",             "cheese"),
        entry("iogurte",            "yogurt"),
        entry("banana",             "banana"),
        entry("maçã",               "apple"),
        entry("maca",               "apple"),
        entry("laranja",            "orange"),
        entry("abacaxi",            "pineapple"),
        entry("uva",                "grape"),
        entry("morango",            "strawberry"),
        entry("melancia",           "watermelon"),
        entry("manga",              "mango"),
        entry("mamão",              "papaya"),
        entry("mamao",              "papaya"),
        entry("alface",             "lettuce"),
        entry("tomate",             "tomato"),
        entry("cenoura",            "carrot"),
        entry("brócolis",           "broccoli"),
        entry("brocolis",           "broccoli"),
        entry("espinafre",          "spinach"),
        entry("couve",              "kale"),
        entry("cebola",             "onion"),
        entry("alho",               "garlic"),
        entry("azeite",             "olive oil"),
        entry("manteiga",           "butter"),
        entry("açúcar",             "sugar"),
        entry("acucar",             "sugar"),
        entry("mel",                "honey"),
        entry("amendoim",           "peanut"),
        entry("amêndoa",            "almond"),
        entry("amendoa",            "almond"),
        entry("castanha",           "chestnut"),
        entry("nozes",              "walnut"),
        entry("macarrão",           "pasta"),
        entry("macarrao",           "pasta"),
        entry("linguiça",           "sausage"),
        entry("linguica",           "sausage"),
        entry("presunto",           "ham"),
        entry("peru",               "turkey"),
        entry("abacate",            "avocado"),
        entry("coco",               "coconut"),
        entry("quinoa",             "quinoa"),
        entry("lentilha",           "lentils"),
        entry("inhame",             "yam"),
        entry("whey",               "whey protein"),
        entry("proteína do soro",   "whey protein"),
        entry("proteina do soro",   "whey protein")
    );

    // ── EN → PT keywords — longest-first for correct matching ────────────────
    private static final List<Map.Entry<String, String>> EN_TO_PT = List.of(
        entry("sweet potato",    "Batata-Doce"),
        entry("chicken breast",  "Frango, Peito"),
        entry("chicken thigh",   "Frango, Coxa"),
        entry("chicken wing",    "Frango, Asa"),
        entry("ground beef",     "Carne Moída"),
        entry("beef tenderloin", "Filé Mignon"),
        entry("beef sirloin",    "Alcatra"),
        entry("beef rump cap",   "Picanha"),
        entry("peanut butter",   "Pasta de Amendoim"),
        entry("black beans",     "Feijão Preto"),
        entry("pinto beans",     "Feijão Carioca"),
        entry("kidney beans",    "Feijão"),
        entry("chickpeas",       "Grão de Bico"),
        entry("brown rice",      "Arroz Integral"),
        entry("white rice",      "Arroz Branco"),
        entry("turkey breast",   "Peito de Peru"),
        entry("skim milk",       "Leite Desnatado"),
        entry("whole milk",      "Leite Integral"),
        entry("olive oil",       "Azeite de Oliva"),
        entry("whey protein",    "Whey Protein"),
        entry("chicken",         "Frango"),
        entry("rice",            "Arroz"),
        entry("beans",           "Feijão"),
        entry("egg",             "Ovo"),
        entry("beef",            "Carne Bovina"),
        entry("salmon",          "Salmão"),
        entry("tuna",            "Atum"),
        entry("tilapia",         "Tilápia"),
        entry("fish",            "Peixe"),
        entry("potato",          "Batata"),
        entry("oats",            "Aveia"),
        entry("bread",           "Pão"),
        entry("milk",            "Leite"),
        entry("cheese",          "Queijo"),
        entry("yogurt",          "Iogurte"),
        entry("banana",          "Banana"),
        entry("apple",           "Maçã"),
        entry("orange",          "Laranja"),
        entry("pineapple",       "Abacaxi"),
        entry("grape",           "Uva"),
        entry("strawberry",      "Morango"),
        entry("watermelon",      "Melancia"),
        entry("mango",           "Manga"),
        entry("papaya",          "Mamão"),
        entry("lettuce",         "Alface"),
        entry("tomato",          "Tomate"),
        entry("carrot",          "Cenoura"),
        entry("broccoli",        "Brócolis"),
        entry("spinach",         "Espinafre"),
        entry("kale",            "Couve"),
        entry("onion",           "Cebola"),
        entry("garlic",          "Alho"),
        entry("butter",          "Manteiga"),
        entry("sugar",           "Açúcar"),
        entry("honey",           "Mel"),
        entry("peanut",          "Amendoim"),
        entry("almond",          "Amêndoa"),
        entry("walnut",          "Nozes"),
        entry("chestnut",        "Castanha"),
        entry("pasta",           "Macarrão"),
        entry("sausage",         "Linguiça"),
        entry("ham",             "Presunto"),
        entry("turkey",          "Peru"),
        entry("avocado",         "Abacate"),
        entry("coconut",         "Coco"),
        entry("quinoa",          "Quinoa"),
        entry("lentils",         "Lentilha"),
        entry("cassava",         "Mandioca"),
        entry("yam",             "Inhame")
    );

    // ── Cooking method suffixes (EN keyword → PT label) ──────────────────────
    private static final List<Map.Entry<String, String>> COOKING = List.of(
        entry("raw",      "cru"),
        entry("roasted",  "assado"),
        entry("grilled",  "grelhado"),
        entry("broiled",  "grelhado"),
        entry("boiled",   "cozido"),
        entry("cooked",   "cozido"),
        entry("fried",    "frito"),
        entry("baked",    "assado"),
        entry("smoked",   "defumado"),
        entry("steamed",  "no vapor"),
        entry("dried",    "seco")
    );

    /**
     * Translates a Portuguese (or already-English) query to English for USDA.
     * Multi-word phrases take priority. Returns the original if nothing matches.
     */
    public static String toEnglishQuery(String ptQuery) {
        String norm = ptQuery.trim().toLowerCase();
        for (Map.Entry<String, String> e : PT_TO_EN) {
            if (norm.contains(e.getKey())) {
                String translated = norm.replace(e.getKey(), e.getValue()).trim();
                return Character.toUpperCase(translated.charAt(0)) + translated.substring(1);
            }
        }
        // Already English or an unmapped Portuguese term — send as-is
        return ptQuery.trim();
    }

    /**
     * Translates a USDA English food description to a concise Portuguese display name.
     * Appends a cooking-method suffix when detectable from the original description.
     * Falls back to the original USDA name when no keyword matches.
     */
    public static String toPortugueseName(String usdaName) {
        String lower = usdaName.toLowerCase();

        String ptBase = null;
        for (Map.Entry<String, String> kw : EN_TO_PT) {
            if (lower.contains(kw.getKey())) {
                ptBase = kw.getValue();
                break;
            }
        }
        if (ptBase == null) return usdaName;

        for (Map.Entry<String, String> cm : COOKING) {
            if (lower.contains(cm.getKey())) {
                return ptBase + ", " + cm.getValue();
            }
        }
        return ptBase;
    }
}
