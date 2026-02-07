package com.mrumbl.backend.repository.store;

import com.mrumbl.backend.domain.Store;

import java.math.BigDecimal;
import java.util.List;

public interface StoreRepositoryCustom {
    List<Store> searchByKeyword(String keyword);
    List<Store> findNearbyStores(BigDecimal x, BigDecimal y, Integer r);
    List<Store> findAllStoresOrderedByDistanceFromDefaultStore();
}
