package com.example.zooseeker;

import java.util.List;

public interface DirectionObserver {
    void updateDirection (Direction direction);
    void updateOrder(List<String> exhibitIds);
}