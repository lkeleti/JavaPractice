-- --------------------------------------------------------
-- Hoszt:                        127.0.0.1
-- Szerver verzió:               10.9.2-MariaDB-1:10.9.2+maria~ubu2204 - mariadb.org binary distribution
-- Szerver OS:                   debian-linux-gnu
-- HeidiSQL Verzió:              12.0.0.6468
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Struktúra mentése tábla invotrax. barcode
CREATE TABLE IF NOT EXISTS `barcode` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) DEFAULT NULL,
  `code` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK8o8myvieuquqfs0euxcv7ktm9` (`code`),
  KEY `FK504tslhamimvlwyaqnxpsh0cr` (`product_id`),
  CONSTRAINT `FK504tslhamimvlwyaqnxpsh0cr` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.barcode: ~0 rows (hozzávetőleg)

-- Struktúra mentése tábla invotrax. flyway_schema_history
CREATE TABLE IF NOT EXISTS `flyway_schema_history` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT current_timestamp(),
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.flyway_schema_history: ~0 rows (hozzávetőleg)

-- Struktúra mentése tábla invotrax. inventory
CREATE TABLE IF NOT EXISTS `inventory` (
  `received_at` date DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `supplier_id` bigint(20) DEFAULT NULL,
  `invoice_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6mcgjlsnq4bm6or8o10jhali3` (`supplier_id`),
  CONSTRAINT `FK6mcgjlsnq4bm6or8o10jhali3` FOREIGN KEY (`supplier_id`) REFERENCES `partner` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.inventory: ~0 rows (hozzávetőleg)

-- Struktúra mentése tábla invotrax. inventory_item
CREATE TABLE IF NOT EXISTS `inventory_item` (
  `gross_price` decimal(38,2) DEFAULT NULL,
  `net_price` decimal(38,2) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `warranty_period_months` int(11) DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `inventory_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKk5dyq25q0q8qyxr17iw854bqm` (`inventory_id`),
  KEY `FKnlagkg4wldbng04fndb117wai` (`product_id`),
  CONSTRAINT `FKk5dyq25q0q8qyxr17iw854bqm` FOREIGN KEY (`inventory_id`) REFERENCES `inventory` (`id`),
  CONSTRAINT `FKnlagkg4wldbng04fndb117wai` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.inventory_item: ~0 rows (hozzávetőleg)

-- Struktúra mentése tábla invotrax. invoice
CREATE TABLE IF NOT EXISTS `invoice` (
  `due_date` date DEFAULT NULL,
  `fulfillment_at` date DEFAULT NULL,
  `gross_total` decimal(38,2) DEFAULT NULL,
  `issued_at` date DEFAULT NULL,
  `net_total` decimal(38,2) DEFAULT NULL,
  `buyer_id` bigint(20) DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `invoice_type_id` bigint(20) DEFAULT NULL,
  `payment_method_id` bigint(20) DEFAULT NULL,
  `seller_id` bigint(20) DEFAULT NULL,
  `invoice_number` varchar(255) DEFAULT NULL,
  `pdf_password` varchar(255) DEFAULT NULL,
  `pdf_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7fm3nyllaec5lnxd8w22cnj8w` (`buyer_id`),
  KEY `FKqss90tikrowtmcc9gw6hegq7d` (`invoice_type_id`),
  KEY `FKrgxmd0sscce0tgfckpcoydrwl` (`payment_method_id`),
  KEY `FKp9udt7fsap2ft6dcy7miopol1` (`seller_id`),
  CONSTRAINT `FK7fm3nyllaec5lnxd8w22cnj8w` FOREIGN KEY (`buyer_id`) REFERENCES `partner` (`id`),
  CONSTRAINT `FKp9udt7fsap2ft6dcy7miopol1` FOREIGN KEY (`seller_id`) REFERENCES `partner` (`id`),
  CONSTRAINT `FKqss90tikrowtmcc9gw6hegq7d` FOREIGN KEY (`invoice_type_id`) REFERENCES `invoice_type` (`id`),
  CONSTRAINT `FKrgxmd0sscce0tgfckpcoydrwl` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_method` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.invoice: ~0 rows (hozzávetőleg)

-- Struktúra mentése tábla invotrax. invoice_item
CREATE TABLE IF NOT EXISTS `invoice_item` (
  `discount_percent` decimal(38,2) DEFAULT NULL,
  `gross_amount` decimal(38,2) DEFAULT NULL,
  `net_amount` decimal(38,2) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL,
  `unit_price` decimal(38,2) DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `invoice_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `vat_rate_id` bigint(20) DEFAULT NULL,
  `unit` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbu6tmpd0mtgu9wrw5bj5uv09v` (`invoice_id`),
  KEY `FKdlrd6r1hiahn8botv6xhatjc2` (`product_id`),
  KEY `FKj9csbe9vv69cuttqmt411axkl` (`vat_rate_id`),
  CONSTRAINT `FKbu6tmpd0mtgu9wrw5bj5uv09v` FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`id`),
  CONSTRAINT `FKdlrd6r1hiahn8botv6xhatjc2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FKj9csbe9vv69cuttqmt411axkl` FOREIGN KEY (`vat_rate_id`) REFERENCES `vat_rate` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.invoice_item: ~0 rows (hozzávetőleg)

-- Struktúra mentése tábla invotrax. invoice_number_sequence
CREATE TABLE IF NOT EXISTS `invoice_number_sequence` (
  `last_number` int(11) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `invoice_type_id` bigint(20) NOT NULL,
  `invoice_prefix` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9koaegxp5pi3wux0xo90mcot6` (`invoice_type_id`),
  CONSTRAINT `FK9koaegxp5pi3wux0xo90mcot6` FOREIGN KEY (`invoice_type_id`) REFERENCES `invoice_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.invoice_number_sequence: ~0 rows (hozzávetőleg)

-- Struktúra mentése tábla invotrax. invoice_type
CREATE TABLE IF NOT EXISTS `invoice_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKg00m3ha80tpr6yh7x1rl6s83m` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.invoice_type: ~0 rows (hozzávetőleg)

-- Struktúra mentése tábla invotrax. manufacturer
CREATE TABLE IF NOT EXISTS `manufacturer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `website` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK8kkbdoe8nd33usrdbpw18ijs4` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.manufacturer: ~0 rows (hozzávetőleg)

-- Struktúra mentése tábla invotrax. partner
CREATE TABLE IF NOT EXISTS `partner` (
  `balance` decimal(38,2) DEFAULT NULL,
  `default_payment_deadline` int(11) DEFAULT NULL,
  `deleted` bit(1) DEFAULT NULL,
  `is_private` bit(1) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `zip_code_id` bigint(20) DEFAULT NULL,
  `building` varchar(255) DEFAULT NULL,
  `door` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `floor` varchar(255) DEFAULT NULL,
  `house_number` varchar(255) DEFAULT NULL,
  `land_registry_number` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `preferred_payment_method` varchar(255) DEFAULT NULL,
  `staircase` varchar(255) DEFAULT NULL,
  `street_type` varchar(255) DEFAULT NULL,
  `tax_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjnfv9u6lpt0v53owyhtulxe3r` (`zip_code_id`),
  CONSTRAINT `FKjnfv9u6lpt0v53owyhtulxe3r` FOREIGN KEY (`zip_code_id`) REFERENCES `zip_code` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.partner: ~0 rows (hozzávetőleg)

-- Struktúra mentése tábla invotrax. payment_method
CREATE TABLE IF NOT EXISTS `payment_method` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKkcbcnwt3uvb5aqq02tssfow8d` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.payment_method: ~0 rows (hozzávetőleg)

-- Struktúra mentése tábla invotrax. product
CREATE TABLE IF NOT EXISTS `product` (
  `deleted` bit(1) NOT NULL,
  `gross_price` decimal(38,2) DEFAULT NULL,
  `net_price` decimal(38,2) DEFAULT NULL,
  `serial_number_required` bit(1) DEFAULT NULL,
  `stock_quantity` int(11) DEFAULT NULL,
  `warranty_period_months` int(11) DEFAULT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `manufacturer_id` bigint(20) DEFAULT NULL,
  `product_type_id` bigint(20) NOT NULL,
  `vat_rate_id` bigint(20) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `sku` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5cypb0k23bovo3rn1a5jqs6j4` (`category_id`),
  KEY `FK89igr5j06uw5ps04djxgom0l1` (`manufacturer_id`),
  KEY `FKlabq3c2e90ybbxk58rc48byqo` (`product_type_id`),
  KEY `FK71p9uduprr80k8nclnfhqba5m` (`vat_rate_id`),
  CONSTRAINT `FK5cypb0k23bovo3rn1a5jqs6j4` FOREIGN KEY (`category_id`) REFERENCES `product_category` (`id`),
  CONSTRAINT `FK71p9uduprr80k8nclnfhqba5m` FOREIGN KEY (`vat_rate_id`) REFERENCES `vat_rate` (`id`),
  CONSTRAINT `FK89igr5j06uw5ps04djxgom0l1` FOREIGN KEY (`manufacturer_id`) REFERENCES `manufacturer` (`id`),
  CONSTRAINT `FKlabq3c2e90ybbxk58rc48byqo` FOREIGN KEY (`product_type_id`) REFERENCES `product_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.product: ~0 rows (hozzávetőleg)

-- Struktúra mentése tábla invotrax. product_category
CREATE TABLE IF NOT EXISTS `product_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.product_category: ~0 rows (hozzávetőleg)

-- Struktúra mentése tábla invotrax. product_type
CREATE TABLE IF NOT EXISTS `product_type` (
  `manages_stock` bit(1) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.product_type: ~0 rows (hozzávetőleg)

-- Struktúra mentése tábla invotrax. serial_number
CREATE TABLE IF NOT EXISTS `serial_number` (
  `used` bit(1) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `inventory_item_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `serial` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKlmgf3x8yd39r8l6iu6huex6r1` (`inventory_item_id`),
  KEY `FKo7o7ac95fjuyr5tq21isox0s6` (`product_id`),
  CONSTRAINT `FKlmgf3x8yd39r8l6iu6huex6r1` FOREIGN KEY (`inventory_item_id`) REFERENCES `inventory_item` (`id`),
  CONSTRAINT `FKo7o7ac95fjuyr5tq21isox0s6` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.serial_number: ~0 rows (hozzávetőleg)

-- Struktúra mentése tábla invotrax. vat_rate
CREATE TABLE IF NOT EXISTS `vat_rate` (
  `rate` decimal(38,2) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.vat_rate: ~0 rows (hozzávetőleg)

-- Struktúra mentése tábla invotrax. zip_code
CREATE TABLE IF NOT EXISTS `zip_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `city` varchar(255) NOT NULL,
  `zip_code` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tábla adatainak mentése invotrax.zip_code: ~0 rows (hozzávetőleg)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
