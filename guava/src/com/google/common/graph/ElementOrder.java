/*
 * Copyright (C) 2016 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.graph;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Ordering;

import java.util.Comparator;

import javax.annotation.Nullable;

/**
 * Used to represent the order of elements in a data structure that supports different options
 * for iteration order guarantees.
 *
 * <p>Example usage:
 * <pre><code>
 *   MutableGraph<Integer> graph
 *       = GraphBuilder.directed().nodeOrder(ElementOrder.natural()).build();
 * </code></pre>
 */
public class ElementOrder<T> {
  private final Type type;
  @Nullable private final Comparator<T> comparator;

  /**
   * The type of ordering that this object specifies.
   * <ul>
   * <li>UNORDERED: no order is guaranteed.
   * <li>INSERTION: insertion ordering is guaranteed.
   * <li>SORTED: ordering according to a supplied comparator is guaranteed.
   * </ul>
   */
  public enum Type {
    UNORDERED,
    INSERTION,
    SORTED
  }

  private ElementOrder(Type type, @Nullable Comparator<T> comparator) {
    this.type = Preconditions.checkNotNull(type);
    Preconditions.checkArgument((type == Type.SORTED) == (comparator != null),
        "if the type is SORTED, the comparator should be non-null; otherwise, it should be null");
    this.comparator = comparator;
  }

  /**
   * Returns the type of ordering used.
   */
  public Type type() {
    return type;
  }

  /**
   * Returns the {@link Comparator} used.
   *
   * @throws IllegalStateException if no comparator is defined
   */
  public Comparator<T> comparator() {
    if (comparator != null) {
      return comparator;
    }
    throw new IllegalStateException("This ordering does not define a comparator");
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ElementOrder)) {
      return false;
    }

    ElementOrder<?> other = (ElementOrder<?>) o;
    return other.type == this.type
        && Objects.equal(other.comparator, this.comparator);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.type, this.comparator);
  }

  @Override
  public String toString() {
    ToStringHelper helper = MoreObjects.toStringHelper(this)
        .add("type", this.type);
    if (this.comparator != null) {
      helper.add("comparator", this.comparator);
    }
    return helper.toString();
  }

  /**
   * Returns an instance which specifies that no ordering is guaranteed.
   */
  public static final <S> ElementOrder<S> unordered() {
    return new ElementOrder<S>(Type.UNORDERED, null);
  }

  /**
   * Returns an instance which specifies that insertion ordering is guaranteed.
   */
  public static final <S> ElementOrder<S> insertion() {
    return new ElementOrder<S>(Type.INSERTION, null);
  }

  /**
   * Returns an instance which specifies that the natural ordering of the elements is guaranteed.
   */
  public static final <S extends Comparable<S>> ElementOrder<S> natural() {
    return new ElementOrder<S>(Type.SORTED, Ordering.<S>natural());
  }

  /**
   * Returns an instance which specifies that the ordering of the elements is guaranteed to be
   * determined by {@code comparator}.
   */
  public static final <S extends Comparable<S>> ElementOrder<S> sorted(Comparator<S> comparator) {
    return new ElementOrder<S>(Type.SORTED, comparator);
  }
}
