package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.FoodItemResponse;
import com.mauricioandrade.progressor.core.application.ports.FoodSearchPort;
import java.util.Optional;

public class LookupFoodByBarcodeUseCase {

  private final FoodSearchPort foodSearchPort;

  public LookupFoodByBarcodeUseCase(FoodSearchPort foodSearchPort) {
    this.foodSearchPort = foodSearchPort;
  }

  public Optional<FoodItemResponse> execute(String barcode) {
    return foodSearchPort.lookupByBarcode(barcode);
  }
}
