package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.application.dto.FoodItemResponse;
import java.util.List;

public interface FoodSearchPort {

  List<FoodItemResponse> search(String query);
}
