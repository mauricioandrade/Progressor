package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.application.dto.FoodItemResponse;
import java.util.List;
import java.util.Optional;

public interface FoodSearchPort {

  List<FoodItemResponse> search(String query);

  Optional<FoodItemResponse> lookupByBarcode(String barcode);
}
