// フロントエンド API インタラクションの型定義。リクエストとレスポンスのデータ構造を定義しており、この部分の型はバックエンドの DTO と高度に一致させる必要がある。
export interface QuoteRequest {
    driverAge: number;
    licenseColor: 'GOLD' | 'BLUE' | 'GREEN';
    usageType: 'PRIVATE' | 'COMMUTE' | 'BUSINESS';
    annualMileage: number;
    driverRange: 'SELF' | 'COUPLE' | 'FAMILY' | 'ANYONE';
    hasCurrentInsurance: boolean;
    grade?: number;
    accidentTerm?: number;
    maker: string;
    carName: string;
    firstRegistrationYearMonth: string;
    vehicleType: 'COMPACT' | 'SEDAN' | 'MINIVAN' | 'SUV' | 'KEI';
    vehicleInsurance: boolean;
    propertyDamageLimit: 'UNLIMITED' | 'THIRTY_MILLION';
    personalInjuryAmount: 'THIRTY_MILLION' | 'FIFTY_MILLION' | 'UNLIMITED';
    lawyerOption: boolean;
    roadService: boolean;
}

export interface QuoteBreakdownDto {
    itemName: string;
    rate: number | null;
    amount: number | null;
}

export interface QuoteResponse {
    quoteNo: string;
    annualPremium: number;
    monthlyPremium: number;
    breakdowns: QuoteBreakdownDto[];
    createdAt: string;
}

export interface ErrorDetails {
    [key: string]: string;
}

export interface ErrorResponse {
    code: string;
    message: string;
    details: ErrorDetails | null;
}
