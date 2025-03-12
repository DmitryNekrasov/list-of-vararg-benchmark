package listofvararg

fun <T> listOfVararg(vararg elements: T): List<T> = if (elements.size > 0) elements.asList() else emptyList()