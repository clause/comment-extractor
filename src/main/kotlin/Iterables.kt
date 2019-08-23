fun <T> Iterable<T>.contiguous(f: (T, T) -> Boolean): Sequence<List<T>> = sequence {
    var run = mutableListOf<T>()
    for (element in this@contiguous) {
        when {
            run.isEmpty() -> run.add(element)
            f(run.last(), element) -> run.add(element)
            else -> {
                yield(run)
                run = mutableListOf(element)
            }
        }
    }
    if(run.isNotEmpty()) {
        yield(run)
    }
}