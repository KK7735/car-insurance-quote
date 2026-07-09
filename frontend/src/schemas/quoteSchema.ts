import { z } from 'zod';

export const step1Schema = z.object({
  driverAge: z.number({ message: '18 以上の値にしてください' })
    .min(18, '18 以上の値にしてください')
    .max(100, '100 以下の値にしてください'),
  licenseColor: z.enum(['GOLD', 'BLUE', 'GREEN'], { message: '必須項目です' }),
  usageType: z.enum(['PRIVATE', 'COMMUTE', 'BUSINESS'], { message: '必須項目です' }),
  annualMileage: z.number({ message: '数値を入力してください' })
    .min(0, '0 以上の値にしてください')
    .max(30000, '30000 以下の値にしてください'),
  driverRange: z.enum(['SELF', 'COUPLE', 'FAMILY', 'ANYONE'], { message: '必須項目です' })
});

export const step2Schema = z.object({
  hasCurrentInsurance: z.preprocess((val) => val === 'true' || val === true, z.boolean()),
  grade: z.number({ message: '数値を入力してください' }).min(1).max(20).optional(),
  accidentTerm: z.number({ message: '数値を入力してください' }).min(0).max(6).optional()
}).superRefine((data, ctx) => {
  // superRefine により複雑なクロスフィールドバリデーション（依存バリデーション）を実現している：「現在加入中の保険がある」が true の場合のみ、等級と事故歴が必須項目となる。
  if (data.hasCurrentInsurance) {
    if (data.grade === undefined || data.grade < 1 || data.grade > 20 || isNaN(data.grade)) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ['grade'],
        message: '現在加入ありの場合、等級(1-20)は必須です',
      });
    }
    if (data.accidentTerm === undefined || data.accidentTerm < 0 || data.accidentTerm > 6 || isNaN(data.accidentTerm)) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ['accidentTerm'],
        message: '現在加入ありの場合、事故有係数期間(0-6)は必須です',
      });
    }
  }
});

export const step3Schema = z.object({
  maker: z.string().min(1, '必須項目です').max(50, '50文字以内で入力してください'),
  carName: z.string().min(1, '必須項目です').max(50, '50文字以内で入力してください'),
  firstRegistrationYearMonth: z.string().regex(/^\d{4}-\d{2}$/, 'YYYY-MM 形式で入力してください'),
  vehicleType: z.enum(['COMPACT', 'SEDAN', 'MINIVAN', 'SUV', 'KEI'], { message: '必須項目です' }),
  vehicleInsurance: z.preprocess((val) => val === 'true' || val === true, z.boolean())
}).superRefine((data, ctx) => {
  // ビジネスルールバリデーション：初度登録年月を未来の時間にすることはできない。Zod のカスタムバリデーションロジックはバックエンドの複雑な判定を完全に代替し、即時的なフロントエンドのフィードバックを提供できる。
  const match = data.firstRegistrationYearMonth.match(/^(\d{4})-(\d{2})$/);
  if (match) {
    const year = parseInt(match[1], 10);
    const month = parseInt(match[2], 10);
    const inputDate = new Date(year, month - 1);
    const today = new Date();
    if (inputDate > today) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ['firstRegistrationYearMonth'],
        message: '未来の年月は入力できません'
      });
    }
  }
});

export const step4Schema = z.object({
  propertyDamageLimit: z.enum(['UNLIMITED', 'THIRTY_MILLION'], { message: '必須項目です' }),
  personalInjuryAmount: z.enum(['THIRTY_MILLION', 'FIFTY_MILLION', 'UNLIMITED'], { message: '必須項目です' }),
  lawyerOption: z.preprocess((val) => val === 'true' || val === true, z.boolean()),
  roadService: z.preprocess((val) => val === 'true' || val === true, z.boolean())
});

// Zod の intersection (`and`) を利用して4つのステップの Schema を1つの大きな Schema に組み合わせ、最終的に送信されるデータがすべてのフェーズの制約を満たすことを保証する。
export const quoteSchema = step1Schema.and(step2Schema).and(step3Schema).and(step4Schema);
export type QuoteFormValues = z.infer<typeof quoteSchema>;
