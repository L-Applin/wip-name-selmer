package com.applin.selmer.util;

import java.io.Serializable;

public interface Maybe<T extends Serializable> extends Serializable {

   static <T extends Serializable> Maybe<T> just(T value) { return new Just<>(value); }
   static <T extends Serializable> Maybe<T> nothing()     { return new Nothing<>(); }

   boolean isJust();

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
}

class Nothing<T extends Serializable> implements Maybe<T> {

   @Override
   public boolean isJust() {
      return false;
   }
}