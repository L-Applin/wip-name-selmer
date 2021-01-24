package com.applin.selmer.util;

import java.io.Serializable;
import java.util.function.Function;

public interface Maybe<T extends Serializable> extends Serializable {

   static <T extends Serializable> Maybe<T> just(T value) { return new Just<>(value); }
   static <T extends Serializable> Maybe<T> nothing()     { return new Nothing<>(); }

   boolean isJust();

   default T getOrElse(T elseValue) {
      if (isNothing()) return elseValue;
      return ((Just<T>) this).value;
   }

   default <U extends Serializable> Maybe<U> map (Function<T, U> mapper) {
      if (isNothing()) return (Maybe<U>) this;
      T value = ((Just<T>)this).value;
      return new Just<>(mapper.apply(value));
   }

   default boolean isNothing() { return !isJust(); }

}

class Just<T extends Serializable> implements Maybe<T> {
   protected T value;

   public Just(T value) {
      this.value = value;
   }

   @Override
   public boolean isJust() {
      return true;
   }

   @Override
   public String toString() {
      return "(Just " + value.toString() + ")";
   }
}

class Nothing<T extends Serializable> implements Maybe<T> {

   @Override
   public boolean isJust() {
      return false;
   }

   @Override
   public String toString() {
      return "(Nothing)";
   }
}