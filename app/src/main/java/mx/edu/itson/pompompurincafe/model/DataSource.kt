package mx.edu.itson.pompompurincafe.model

import mx.edu.itson.pompompurincafe.R

object DataSource {
    val menuCompleto = listOf(
        Platillo(1, "Happy Birthday ♪ Strawberry Mousse", "A simple, but moorsihly sweet and fluffy strawberry mousse served in a Pompompurin shaped bowl.", 88.88, R.drawable.menu1, "Postres"),
        Platillo(2, "Happy Birthday ♪ Flower Bouquet Bisque", "Witness Pompompurin striding across a sea of bisque to receive his very own birthday bouquet!", 178.89, R.drawable.menu2, "Platillos"),
        Platillo(3, "Ode Yukako x Pompompurin Lemonade", "The gentle, pleasurable tang of the lemon flavor is sure to have you fizzing with excitement.", 61.88, R.drawable.menu3, "Bebidas"),
        Platillo(4, "Chocolate Banana Parfait", "A special parfait of Purin, chocolate and banana. At the base is a fluffy sponge cake.", 111.38, R.drawable.menu21, "Postres"),
        Platillo(5, "Mango Soda with Icecream", "Mango Soda with Icecream", 84.38, R.drawable.menu5, "Bebidas"),
        Platillo(6, "Fluffy Souffle Omelette Rice", "Omurice wrapped in a fluffy soufflé omelette with demi-glace sauce.", 212.64, R.drawable.menu20, "Platillos"),
        Platillo(7, "Pompompurin´s Mango Parfait", "A special parfait of Purin, diced mangos, ice cream and much more.", 163.14, R.drawable.menu7, "Postres"),
        Platillo(8, "Bagel´s Special Pancake Tower", "A decadent dessert with five layers of pancakes filled with various sauces.", 167.64, R.drawable.menu8, "Postres"),
        Platillo(9, "Pompompurin´s Beef Stroganoff", "A slow-cooked stroganoff with a deep flavour! Served with a beret-shaped hamburger patty.", 156.39, R.drawable.menu16, "Platillos"),
        Platillo(10, "Strawberry Soda with Icecream", "Strawberry Soda with Icecream", 84.38, R.drawable.menu19, "Bebidas"),
        Platillo(11, "I am Purin ♪ Pompompurin Pudding", "Custard pudding of Pompompurin himself. Take off the biscuit beret and enjoy.", 56.25, R.drawable.menu11, "Postres")
    )

    // Datos de ejemplo para las órdenes activas
    val ordenesEjemplo = listOf(
        Orden(id = "1", mesa = 12, numPersonas = 2, tipo = "MESA"),
        Orden(id = "2", mesa = 10, numPersonas = 1, tipo = "MESA"),
        Orden(id = "3", mesa = 3, numPersonas = 1, tipo = "PERSONA"),
        Orden(id = "4", mesa = 4, numPersonas = 2, tipo = "PERSONA")
    )
}
