package net.davtyan.paypalmobilesdk.items


//empty items list for the cart
class Cart {

    private var myCart: MutableList<Item> = mutableListOf()

    //    clearing all the items in the class. using for the cart
    fun wipe() {
        myCart.clear()
    }

    //    add an items to the cart
    fun addItem(item: Item) {
        myCart.add(item)
    }

    //    get an items by index from the cart
    fun getItem(i: Int = 0) : Item{
        return myCart[i]
    }

    //    get a size of the cart
    fun getCartSize() : Int{
        return myCart.size
    }

    //    refreshing the cart text view, calculating total price for checkout
    fun updateCart(): Pair<Int, Int> {
        var totalPriceInCart = 0
        for (i in myCart) {
            totalPriceInCart += i.itemPrice
        }
        return Pair(myCart.size, totalPriceInCart)
    }

}