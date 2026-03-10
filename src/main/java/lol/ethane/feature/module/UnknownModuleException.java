package lol.ethane.feature.module;

import lombok.Generated;

public class UnknownModuleException extends Exception {
   private final String id;

   public UnknownModuleException(String id) {
      super(String.format("Module with the id %s could not be found.", id));
      this.id = id;
   }

   @Generated
   public String getId() {
      return this.id;
   }
}
