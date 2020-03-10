package pl.utp.pss.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDate;

@Data
@ToString(exclude = "user")
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Delegation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @NotNull
    private LocalDate dateTimeStart;

    @NotNull
    private LocalDate dateTimeStop;

    @Column(columnDefinition = "double default 30")
    private double travelDietAmount = 30;

    @Column(columnDefinition = "integer default 0")
    private int breakfastNumber = 0;

    @Column(columnDefinition = "integer default 0")
    private int dinnerNumber = 0;

    @Column(columnDefinition = "integer default 0")
    private int supperNumber = 0;

    private TransportType transportType;

    private double ticketPrice;

    private AutoCapacity autoCapacity;

    private double km;

    private double accommodationPrice;

    private double otherOutlayDesc;

    private double otherOutlayPrice;

    @ManyToOne
    private User user;

    public Delegation(@Null String description, @NotNull LocalDate dateTimeStart, @NotNull LocalDate dateTimeStop,
                      double travelDietAmount, int breakfastNumber, int dinnerNumber, int supperNumber,
                      TransportType transportType, double ticketPrice, AutoCapacity autoCapacity, double km,
                      double accommodationPrice, double otherOutlayDesc, double otherOutlayPrice) {
        this.description = description;
        this.dateTimeStart = dateTimeStart;
        this.dateTimeStop = dateTimeStop;
        this.travelDietAmount = travelDietAmount;
        this.breakfastNumber = breakfastNumber;
        this.dinnerNumber = dinnerNumber;
        this.supperNumber = supperNumber;
        this.transportType = transportType;
        this.ticketPrice = ticketPrice;
        this.autoCapacity = autoCapacity;
        this.km = km;
        this.accommodationPrice = accommodationPrice;
        this.otherOutlayDesc = otherOutlayDesc;
        this.otherOutlayPrice = otherOutlayPrice;

        if (this.transportType.equals(TransportType.CAR)) {
            this.ticketPrice = 0.0;
        } else {
            this.autoCapacity = AutoCapacity.NONE;
            this.km = 0.0;
        }
    }
}