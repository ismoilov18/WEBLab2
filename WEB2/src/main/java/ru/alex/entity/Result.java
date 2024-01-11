package ru.alex.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;


@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Data
public class Result implements Serializable {
    int number;
    Point point;
    boolean successful;
    LocalDateTime time;
    int executionTimeInMicros;

    @Override
    public String toString() {
        return String.format("""
                <tr>
                    <td>%s</td>
                    <td>%.3f</td>
                    <td>%.3f</td>
                    <td>%.3f</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                <tr>
                """,
                number, point.getX(), point.getY(),
                point.getR(), successful ? "Да" : "Нет",
                point.isClicked() ? "Нажатие" : "Форма",
                time.format(Constant.DATE_FORMATTER),
                executionTimeInMicros
        );
    }
}
