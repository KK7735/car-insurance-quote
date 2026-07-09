package com.example.carinsurance.config;

import com.example.carinsurance.domain.entity.AdminUser;
import com.example.carinsurance.domain.repository.AdminUserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// データ初期化コンポーネント。CommandLineRunner を利用して Spring Boot の起動完了直後に実行され、データベースに少なくとも1人のデフォルト管理者が存在することを保証する。
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Override
    public void run(String... args) throws Exception {
        // テーブルが空の場合にのみシードを実行する。本番環境では通常、Flyway や Liquibase などの専門的なデータベースマイグレーションツールを使用するが、ここでは課題のデモとして実装している。
        if (adminUserRepository.count() == 0) {
            AdminUser admin = AdminUser.builder()
                    .username("admin")
                    .passwordHash(BCrypt.hashpw("password", BCrypt.gensalt()))
                    .build();
            adminUserRepository.save(admin);
            System.out.println("Default admin user created: admin / password");
        }
    }
}
