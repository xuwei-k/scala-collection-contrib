package scala
package collection
package mutable

/**
  * A mutable multiset whose elements are sorted according to a given ordering.
  *
  * @tparam A Type of elements
  */
class SortedMultiSet[A] private (elems: SortedMap[A, Int])(implicit val ordering: Ordering[A])
  extends MultiSet[A]
    with collection.SortedMultiSet[A]
    with collection.SortedMultiSetOps[A, SortedMultiSet, SortedMultiSet[A]]
    with MultiSetOps[A, MultiSet, SortedMultiSet[A]]
    with Growable[A]
    with Shrinkable[A] {

  def occurrences: collection.SortedMap[A, Int] = elems

  override def sortedIterableFactory: SortedIterableFactory[SortedMultiSet] = SortedMultiSet
  override protected def fromSpecific(coll: IterableOnce[A]): SortedMultiSet[A] = sortedIterableFactory.from(coll)
  override protected def newSpecificBuilder: mutable.Builder[A, SortedMultiSet[A]] = sortedIterableFactory.newBuilder[A]
  override def empty: SortedMultiSet[A] = sortedIterableFactory.empty
  override def withFilter(p: A => Boolean): SortedMultiSetOps.WithFilter[A, MultiSet, SortedMultiSet] =
    new SortedMultiSetOps.WithFilter(this, p)

  def rangeImpl(from: Option[A], until: Option[A]): SortedMultiSet[A] =
    new SortedMultiSet(elems.rangeImpl(from, until))

  def addOne(elem: A): this.type = {
    elems.updateWith(elem) {
      case None    => Some(1)
      case Some(n) => Some(n + 1)
    }
    this
  }

  def subtractOne(elem: A): this.type = {
    elems.updateWith(elem) {
      case Some(n) => if (n > 1) Some(n - 1) else None
      case None => None
    }
    this
  }

  def clear(): Unit = elems.clear()
}

object SortedMultiSet extends SortedIterableFactory[SortedMultiSet] {

  def from[E: Ordering](it: IterableOnce[E]): SortedMultiSet[E] = (newBuilder[E] ++= it).result()

  def empty[A: Ordering]: SortedMultiSet[A] = new SortedMultiSet[A](SortedMap.empty[A, Int])

  def newBuilder[A: Ordering]: Builder[A, SortedMultiSet[A]] = new GrowableBuilder[A, SortedMultiSet[A]](empty)

}