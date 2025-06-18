package xyz.playground.stl_web_app.Service;

import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Constants.GameType;

@Service
public class NumberPickValidationService {

    public void validateNumberPicks(String numberPicks, String type) {

        if (numberPicks == null || numberPicks.isEmpty()) {
            throw new IllegalArgumentException("Number picks cannot be empty");
        }

        GameType gameType = GameType.valueOf(type);

        String[] parts = numberPicks.split("-");
        if (parts.length != gameType.getSize()) {
            throw new IllegalArgumentException(gameType.getValue() + " number picks must have exactly " + gameType.getSize() + " numbers");
        }

        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);

                if (!part.equals(String.valueOf(num))) {
                    throw new IllegalArgumentException("Please remove leading or excess zeroes on number picks");
                }

                if (num < gameType.getMinRange() || num > gameType.getMaxRange()) {
                    throw new IllegalArgumentException(gameType.getValue() + " number picks must be between "
                            + gameType.getMinRange() + " and " + gameType.getMaxRange());
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number pick format");
        }
    }



}
