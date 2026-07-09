package com.example.carinsurance.domain.service.calculation;

import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

// 保険料計算の実行チェーンを定義する（Chain of Responsibilityパターン）。リストの順序は最終的な実行順序ではなく、各StepのgetOrder()によって決定される。
public class CalculationSteps {
    public static List<CalculationStep> getAllSteps() {
        return Arrays.asList(
                new AgeStep(), new LicenseColorStep(), new UsageTypeStep(),
                new MileageStep(), new DriverRangeStep(), new GradeStep(),
                new AccidentTermStep(), new VehicleTypeStep(), new AdditionStep()
        );
    }
}

@Component class AgeStep implements CalculationStep {
    @Override public void calculate(QuoteContext context) {
        int age = context.getCommand().getQuote().getDriverAge();
        if (age >= 18 && age <= 25) context.applyRate("AGE", "AGE_18_25");
        else if (age >= 26 && age <= 34) context.applyRate("AGE", "AGE_26_34");
        else if (age >= 35 && age <= 59) context.applyRate("AGE", "AGE_35_59");
        else if (age >= 60) context.applyRate("AGE", "AGE_60_OVER");
    }
    @Override public int getOrder() { return 10; }
}

@Component class LicenseColorStep implements CalculationStep {
    @Override public void calculate(QuoteContext context) {
        context.applyRate("LICENSE", context.getCommand().getQuote().getLicenseColor());
    }
    @Override public int getOrder() { return 20; }
}

@Component class UsageTypeStep implements CalculationStep {
    @Override public void calculate(QuoteContext context) {
        context.applyRate("USAGE", context.getCommand().getQuote().getUsageType());
    }
    @Override public int getOrder() { return 30; }
}

@Component class MileageStep implements CalculationStep {
    @Override public void calculate(QuoteContext context) {
        int mileage = context.getCommand().getQuote().getAnnualMileage();
        if (mileage >= 0 && mileage <= 5000) context.applyRate("MILEAGE", "MILEAGE_0_5000");
        else if (mileage >= 5001 && mileage <= 10000) context.applyRate("MILEAGE", "MILEAGE_5001_10000");
        else if (mileage >= 10001) context.applyRate("MILEAGE", "MILEAGE_10001_OVER");
    }
    @Override public int getOrder() { return 40; }
}

@Component class DriverRangeStep implements CalculationStep {
    @Override public void calculate(QuoteContext context) {
        context.applyRate("DRIVER_RANGE", context.getCommand().getQuote().getDriverRange());
    }
    @Override public int getOrder() { return 50; }
}

@Component class GradeStep implements CalculationStep {
    @Override public void calculate(QuoteContext context) {
        // 現在のノンフリート等級は、「現在加入中の保険がある」ことを前提条件としてのみ適用される。
        if (Boolean.TRUE.equals(context.getCommand().getQuote().getHasCurrentInsurance())) {
            Integer grade = context.getCommand().getQuote().getGrade();
            if (grade != null) {
                if (grade >= 1 && grade <= 5) context.applyRate("GRADE", "GRADE_1_5");
                else if (grade >= 6 && grade <= 10) context.applyRate("GRADE", "GRADE_6_10");
                else if (grade >= 11 && grade <= 15) context.applyRate("GRADE", "GRADE_11_15");
                else if (grade >= 16 && grade <= 20) context.applyRate("GRADE", "GRADE_16_20");
            }
        }
    }
    @Override public int getOrder() { return 60; }
}

@Component class AccidentTermStep implements CalculationStep {
    @Override public void calculate(QuoteContext context) {
        // 事故歴の判定は等級と同様に、加入中の保険のコンテキストに基づく必要がある。
        if (Boolean.TRUE.equals(context.getCommand().getQuote().getHasCurrentInsurance())) {
            Integer term = context.getCommand().getQuote().getAccidentTerm();
            if (term != null && term >= 1) context.applyRate("ACCIDENT_TERM", "HAS_TERM");
        }
    }
    @Override public int getOrder() { return 70; }
}

@Component class VehicleTypeStep implements CalculationStep {
    @Override public void calculate(QuoteContext context) {
        context.applyRate("VEHICLE_TYPE", context.getCommand().getQuote().getVehicleType());
    }
    @Override public int getOrder() { return 80; }
}

@Component class AdditionStep implements CalculationStep {
    @Override public void calculate(QuoteContext context) {
        // すべての特約および追加補償を処理する。以前の乗数係数とは異なり、ここでは定額加算を直接計算する。
        QuoteCalculationCommand cmd = context.getCommand();
        if (Boolean.TRUE.equals(cmd.getQuote().getVehicleInsurance())) {
            context.applyAddition("VEHICLE_INSURANCE", "WITH_INSURANCE");
        }
        if (cmd.getPropertyDamageLimit() != null) {
            context.applyAddition("PROPERTY_DAMAGE", cmd.getPropertyDamageLimit());
        }
        if (cmd.getPersonalInjuryAmount() != null) {
            context.applyAddition("PERSONAL_INJURY", cmd.getPersonalInjuryAmount());
        }
        if (Boolean.TRUE.equals(cmd.getLawyerOption())) {
            context.applyAddition("LAWYER_OPTION", "WITH_OPTION");
        }
        if (Boolean.TRUE.equals(cmd.getRoadService())) {
            context.applyAddition("ROAD_SERVICE", "WITH_SERVICE");
        }
    }
    @Override public int getOrder() { return 90; }
}
