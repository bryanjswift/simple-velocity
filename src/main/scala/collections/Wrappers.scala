package collections

import scala.collection.jcl.CollectionWrapper

class IterableWrapper[T](iterable:Iterable[T]) extends java.lang.Iterable[T] {
	def iterator() = new java.util.Iterator[T] {
		private val delegate = iterable.elements
		def hasNext = delegate.hasNext
		def next() = delegate.next
		def remove = {
			throw new UnsupportedOperationException
		}
	}
}

class VectorWrapper[T](override val underlying:java.util.Vector[T]) extends CollectionWrapper[T] {
	override def transform(f: (T) => T) = {
		for (i <- 0 until underlying.size) {
			underlying.set(i,f(underlying.get(i)))
		}
		true
	}
}

