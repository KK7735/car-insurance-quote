# 自動車保険見積サイト - Swagger UI 利用ガイド (API Reference)

本書は、自動車保険見積サイトのバックエンド API 仕様の確認、および Swagger UI を用いたテスト実行手順について解説する日本語ドキュメントです。

---

## 1. ドキュメント概要 (Overview)

本ドキュメントの目的は以下の通りです。
- バックエンドが提供する RESTful API の仕様（エンドポイント、パラメータ、エラーコードなど）の正確な把握。
- Swagger UI（OpenAPI 3.0）を利用した、ローカル環境でのリアルタイムな API テスト・検証手順の案内。

---

## 2. アクセス手順 (Access URL)

ローカル環境でバックエンドが起動している場合、以下の URL から Swagger UI にアクセスできます。

- **Swagger UI アクセス URL**:  
  👉 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

- **API ベース URL (Base URL)**:  
  `http://localhost:8080`

---

## 3. 認証プロセス (Authentication)

管理者向け API（パスが `/api/admin/**` で始まるエンドポイント）の呼び出しには、JWT（JSON Web Token）による認証が必要です。Swagger UI 上で認証をパスする手順は以下の通りです。

### ステップ 1: 管理者トークンの取得
まず、一般公開されているログイン API を使用して認証用トークンを取得します。

1. Swagger UI 上で `POST /api/admin/login` (Admin API) を開きます。
2. 右上の **「Try it out」** をクリックします。
3. Request body に以下のデフォルト管理者認証情報を入力します：
   ```json
   {
     "username": "admin",
     "password": "password"
   }
   ```
4. **「Execute」** をクリックします。
5. レスポンス（HTTP 200 OK）の `token` の値をコピーします：
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   }
   ```

### ステップ 2: Swagger UI での認証設定 (Authorize)
1. Swagger UI 画面の右上のあたりにある鍵マーク付きの **「Authorize」** ボタンをクリックします。
2. ポップアップが表示されたら、**Value** 入力欄にコピーしたトークン文字列を**そのまま貼り付けます**。
   - *注意*: OpenAPI の設定クラスにて `bearer` 認証方式（Scheme: bearer, Format: JWT）として定義されているため、Swagger UI が自動的に `Bearer ` 接頭辞を付与してリクエストを送信します。**ご自身で `Bearer ` と入力する必要はありません。**
3. **「Authorize」** ボタンをクリックし、ポップアップを閉じます（鍵マークが閉じた状態に変わります）。
4. これで、認証が必要な `/api/admin/**` へのアクセスが許可されます。

---

## 4. 主要な API エンドポイント (Main Endpoints)

| メソッド | パス | 概要 | 認証要否 |
| :--- | :--- | :--- | :---: |
| **POST** | `/api/quotes` | 保険見積の作成（計算およびデータ保存） | 不要 |
| **GET** | `/api/quotes/{quoteNo}` | 見積結果および計算内訳の取得 | 不要 |
| **POST** | `/api/admin/login` | 管理者ログイン（Tokenの取得） | 不要 |
| **GET** | `/api/admin/quotes` | 見積一覧の取得および検索（ページネーション対応） | **要** |
| **GET** | `/api/admin/quotes/{quoteNo}` | 管理者用：見積詳細の取得 | **要** |
| **GET** | `/api/admin/quotes/csv` | 見積データの CSV ダウンロード | **要** |

---

## 5. 各 API 仕様の解説

### 5.1 【一般用】見積作成 (POST `/api/quotes`)
ユーザーが入力した見積条件を検証し、保費計算エンジンで保険料を算出してデータベースに保存します。

- **リクエストボディ (JSON)**:
  `QuoteRequest` オブジェクト。各パラメータのバリデーションルールは以下の通りです。

  | フィールド名 | 型 | 必須 | 制約・バリデーション規則 | 例 |
  | :--- | :---: | :---: | :--- | :--- |
  | `driverAge` | Integer | ◯ | `18` 以上 `100` 以下 | `35` |
  | `licenseColor` | String | ◯ | `"GOLD"`, `"BLUE"`, `"GREEN"` のいずれか | `"GOLD"` |
  | `usageType` | String | ◯ | `"PRIVATE"`, `"COMMUTE"`, `"BUSINESS"` のいずれか | `"PRIVATE"` |
  | `annualMileage` | Integer | ◯ | `0` 以上 `30000` 以下 | `8000` |
  | `driverRange` | String | ◯ | `"SELF"`, `"COUPLE"`, `"FAMILY"`, `"ANYONE"` のいずれか | `"SELF"` |
  | `hasCurrentInsurance`| Boolean | ◯ | 現在他社等の自動車保険に加入しているか | `true` |
  | `grade` | Integer | △ | **現在加入ありの場合必須**。`1` 以上 `20` 以下 | `20` |
  | `accidentTerm` | Integer | △ | **現在加入ありの場合必須**。`0` 以上 `6` 以下 | `0` |
  | `maker` | String | ◯ | 最大50文字 | `"TOYOTA"` |
  | `carName` | String | ◯ | 最大50文字 | `"PRIUS"` |
  | `firstRegistrationYearMonth` | String | ◯ | `YYYY-MM` フォーマット | `"2024-01"` |
  | `vehicleType` | String | ◯ | `"COMPACT"`, `"SEDAN"`, `"MINIVAN"`, `"SUV"`, `"KEI"` のいずれか | `"SEDAN"` |
  | `vehicleInsurance` | Boolean | ◯ | 車両保険の有無 | `false` |
  | `propertyDamageLimit`| String | ◯ | 対物補償限度額：`"UNLIMITED"`, `"THIRTY_MILLION"` | `"UNLIMITED"` |
  | `personalInjuryAmount`| String | ◯ | 人身傷害補償額：`"THIRTY_MILLION"`, `"FIFTY_MILLION"`, `"UNLIMITED"` | `"FIFTY_MILLION"` |
  | `lawyerOption` | Boolean | ◯ | 弁護士費用特約の有無 | `false` |
  | `roadService` | Boolean | ◯ | ロードサービスの有無 | `false` |

  > [!IMPORTANT]
  > **相関バリデーションルール (防雷)**:  
  > `hasCurrentInsurance` が `true` の場合、`grade` (1-20) および `accidentTerm` (0-6) の両方を指定する必要があります。欠落している場合は `400 Bad Request` が返されます。

- **成功時レスポンス (HTTP 201 Created)**:
  ```json
  {
    "quoteNo": "EST202607090001",
    "annualPremium": 40400,
    "monthlyPremium": 3370,
    "breakdowns": [
      {
        "itemName": "基本保険料",
        "rate": null,
        "amount": 50000
      },
      {
        "itemName": "ゴールド免許割引",
        "rate": 0.900,
        "amount": null
      }
    ],
    "createdAt": "2026-07-09T16:17:00"
  }
  ```

---

### 5.2 【一般用】見積結果取得 (GET `/api/quotes/{quoteNo}`)
発行された一意の見積番号 (`quoteNo`) に紐づく見積結果と計算内訳を取得します。

- **パスパラメータ**:
  - `quoteNo` (String, 必須): 例 `"EST202607090001"`
- **成功時レスポンス (HTTP 200 OK)**:
  - 構造は `POST /api/quotes` の成功時レスポンスと同様です。

---

### 5.3 【管理者用】管理者ログイン (POST `/api/admin/login`)
管理者のユーザIDとパスワードを用いて JWT トークンを取得します。

- **リクエストボディ (JSON)**:
  ```json
  {
    "username": "admin",
    "password": "password"
  }
  ```
- **成功時レスポンス (HTTP 200 OK)**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MT..."
  }
  ```

---

### 5.4 【管理者用】見積一覧取得・検索 (GET `/api/admin/quotes`)
これまでに作成された見積データの一覧を検索・取得します（認証必須）。

- **クエリパラメータ**:
  - `quoteNo` (String, 任意): 見積番号の部分一致または完全一致検索用。
  - `page` (Integer, 任意, デフォルト: `0`): 取得するページ番号。
  - `size` (Integer, 任意, デフォルト: `10`): 1ページあたりの表示件数。
- **成功時レスポンス (HTTP 200 OK)**:
  Spring Data の `Page` 規格でラップされた一覧が返却されます。
  ```json
  {
    "content": [
      {
        "quoteNo": "EST202607090001",
        "driverAge": 35,
        "licenseColor": "GOLD",
        "usageType": "PRIVATE",
        "annualMileage": 8000,
        "driverRange": "SELF",
        "hasCurrentInsurance": true,
        "grade": 20,
        "accidentTerm": 0,
        "maker": "TOYOTA",
        "carName": "PRIUS",
        "firstRegistrationYm": "2024-01",
        "vehicleType": "SEDAN",
        "vehicleInsurance": false,
        "annualPremium": 40400,
        "monthlyPremium": 3370,
        "createdAt": "2026-07-09T16:17:00"
      }
    ],
    "pageable": { ... },
    "totalPages": 1,
    "totalElements": 1,
    "last": true,
    "size": 10,
    "number": 0,
    "first": true,
    "numberOfElements": 1,
    "empty": false
  }
  ```

---

### 5.5 【管理者用】見積詳細取得 (GET `/api/admin/quotes/{quoteNo}`)
管理者が特定の見積情報の詳細および内訳を閲覧します（認証必須）。

- **パスパラメータ**:
  - `quoteNo` (String, 必須)
- **成功時レスポンス (HTTP 200 OK)**:
  - 構造は一般用見積取得 (`GET /api/quotes/{quoteNo}`) と同様です。

---

### 5.6 【管理者用】CSVダウンロード (GET `/api/admin/quotes/csv`)
これまでに作成されたすべての見積一覧を CSV 形式で一括ダウンロードします（認証必須）。

- **レスポンスヘッダー**:
  - `Content-Type: text/csv; charset=UTF-8`
  - `Content-Disposition: attachment; filename="quotes.csv"`
- **レスポンスデータ**:
  - UTF-8エンコーディングされた CSV ファイルのバイナリストリーム。

---

## 6. エラーハンドリングとレスポンス

エラー発生時、本システムは常に統一された形式でエラー情報を返却します。

```json
{
  "code": "エラー種別コード",
  "message": "エラーメッセージ",
  "details": {
    "フィールド名": "具体的なエラー詳細"
  }
}
```

### 代表的なエラーパターン
- **`400 Bad Request` (VALIDATION_ERROR)**:  
  パラメータ不足や入力規則違反（例: `driverAge` が17以下、`hasCurrentInsurance` が `true` なのに `grade` が指定されていない等）の場合。
- **`401 Unauthorized` (UNAUTHORIZED)**:  
  `/api/admin/**` にトークンなし、または無効なトークンでアクセスした場合。
- **`404 Not Found` (NOT_FOUND)**:  
  存在しない `quoteNo` を指定してデータを取得しようとした場合。
- **`500 Internal Server Error` (SYSTEM_ERROR)**:  
  システム内部エラー。セキュリティ保護のため、スタックトレースは返されません。

---

## 7. Swagger UI でのテスト実行手順

初心者でも以下の手順に沿うことで、Swagger UI を用いた即時のテストが可能です。

1. **API を選択する**:  
   Swagger UI の画面上で、テストしたい API (例: `POST /api/quotes`) をクリックして展開します。
2. **テストモードにする**:  
   右上の **「Try it out」** ボタンをクリックします。入力欄がアクティブ化（編集可能）されます。
3. **パラメータを入力する**:  
   リクエストパラメータや Request body (JSON) を要件（セクション5を参照）に合わせて編集します。
4. **API を実行する**:  
   青色の **「Execute」** ボタンをクリックします。
5. **結果を確認する**:  
   直下の **「Responses」** セクションに、実行結果が表示されます。
   - **Curl**: 実際に送信された curl コマンド（手動でのターミナル実行用）。
   - **Request URL**: リクエスト先の完全なパス。
   - **Server response**: HTTPステータスコード（例: `200`, `201`, `400`）および JSON 形式のレスポンスボディ。
