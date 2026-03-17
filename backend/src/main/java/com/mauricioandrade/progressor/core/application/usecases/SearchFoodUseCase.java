package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.FoodItemResponse;
import com.mauricioandrade.progressor.core.application.ports.FoodSearchPort;
import java.util.List;

public class SearchFoodUseCase {

  private final FoodSearchPort foodSearchPort;

  public SearchFoodUseCase(FoodSearchPort foodSearchPort) {
    this.foodSearchPort = foodSearchPort;
  }

  public List<FoodItemResponse> execute(String query) {
    return foodSearchPort.search(query);
  }
}
