package xyz.playground.stl_web_app.Service;

import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Constants.GameType;

@Service
public class NumberPickValidationService {

    public void validateNumberPicks(String numberPicks, String type) {

        if (numberPicks == null || numberPicks.isEmpty())
            throw new IllegalArgumentException("Input cannot be empty.");

        GameType gameType = GameType.valueOf(type);

        String[] parts = numberPicks.split("-");
        if (parts.length != gameType.getSize())
            throw new IllegalArgumentException(gameType.getValue() + " input must have exactly " + gameType.getSize() + " numbers. Input: " + numberPicks);

        if(numberPicks.charAt(numberPicks.length() - 1) == '-')
            throw new IllegalArgumentException("Please remove trailing minus symbols (-). Input: " + numberPicks);

        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);

                if (!part.equals(String.valueOf(num)))
                    throw new IllegalArgumentException("Please remove leading or excess zeroes. Input: " + numberPicks);

                if (num < gameType.getMinRange() || num > gameType.getMaxRange()) {
                    throw new IllegalArgumentException(gameType.getValue() + " numbers must be between "
                            + gameType.getMinRange() + " and " + gameType.getMaxRange() + ". Input: " + numberPicks);
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid format. Input: " + numberPicks);
        }
    }

}
