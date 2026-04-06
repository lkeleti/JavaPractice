CREATE TABLE `user_projects` (
  `project_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  KEY `FKajufdspjypencrsbn6knqfyf` (`project_id`),
  KEY `FKr25ilmlcm8ugp8i3rogl6jp0l` (`user_id`),
  CONSTRAINT `FKajufdspjypencrsbn6knqfyf` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `FKr25ilmlcm8ugp8i3rogl6jp0l` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;