CREATE TABLE IF NOT EXISTS `loans` (
  `due_date` date NOT NULL,
  `loan_date` date NOT NULL,
  `return_date` date DEFAULT NULL,
  `book_id` bigint(20) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `borrower_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_book_id` (`book_id`),
  CONSTRAINT `FK_loan_book` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;