CREATE DATABASE IF NOT EXISTS `616157`;
USE `616157`;

-- Tabella dipendente
DROP TABLE IF EXISTS `dipendente`;
CREATE TABLE `dipendente` (
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(64) DEFAULT NULL,
  `ruolo` VARCHAR(45) DEFAULT NULL,
  `sessiontoken` VARCHAR(64) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabella sede logistica
DROP TABLE IF EXISTS `sedelogistica`;
CREATE TABLE `sedelogistica` (
  `nome` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`nome`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabella veicolo
DROP TABLE IF EXISTS `veicolo`;
CREATE TABLE `veicolo` (
  `nome` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`nome`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Tabella viaggio
DROP TABLE IF EXISTS `viaggio`;
CREATE TABLE `viaggio` (
  `dipendente` VARCHAR(45) NOT NULL,
  `veicolo` VARCHAR(45) NOT NULL,
  `data` DATE NOT NULL,
  `partenza` VARCHAR(45) NOT NULL,
  `arrivo` VARCHAR(45) NOT NULL,
  `carico` BLOB,
  PRIMARY KEY (`dipendente`, `veicolo`, `data`, `partenza`, `arrivo`),
  FOREIGN KEY (`dipendente`) REFERENCES `dipendente`(`username`),
  FOREIGN KEY (`veicolo`) REFERENCES `veicolo`(`nome`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `sedelogistica` (`nome`) VALUES
('Milano'),
('Roma'),
('Napoli'),
('Torino'),
('Firenze');

INSERT INTO `veicolo` (`nome`) VALUES
('AB123CD'),
('EF456GH'),
('IJ789KL'),
('MN012OP'),
('QR345ST');

INSERT INTO `dipendente` (`username`, `password`, `ruolo`, `sessiontoken`) VALUES
('marco rossi', 'fIzMhsEWVK8ClFfZD92dATzm+wEe6P2xN0gyJozI2Wc=', 'worker', ''),
('giulia bianchi', '5MLu2KbfAUcmVjHp/yW3D9Dks6JGiWaVsIlYS/POi5A=', 'worker', ''),
('luca verdi', '1w9HeQ9olBR4nu/yMXA0Kcf4ihAhB3WQZGDtvzhYnZA=', 'worker', ''),
('anna neri', 'VVebVXiW0M4XZMR/7WRPmzX1i61iBnSvI/NW2A7QxQM=', 'worker', ''),
('matteo esposito', '/jAeqqxJtGUrjf2fsOkTaDrFYA9ZNwpiYYJKtgi0+tc=', 'worker', ''),
('alessandro','i1VUUrFMjTVwYIlkoc5AZClzF0YhHUXdPJLCH7NcsDk=','admin','');

INSERT INTO `viaggio` (`dipendente`, `veicolo`, `data`, `partenza`, `arrivo`, `carico`) VALUES
('marco rossi', 'AB123CD', '2025-01-01', 'Milano', 'Torino', '[
    {
        "nome":"frigorifero",
        "marca":"samsung",
        "quantity": 5,
        "venditore": "trony"
    },
    {
        "nome":"televisore",
        "marca":"samsung",
        "quantity": 4,
        "venditore": "trony"
    }
]'),
('giulia bianchi', 'EF456GH', '2025-01-02', 'Roma', 'Napoli', '[
    {
        "nome":"frigorifero",
        "marca":"samsung",
        "quantity": 5,
        "venditore": "trony"
    },
    {
        "nome":"televisore",
        "marca":"samsung",
        "quantity": 4,
        "venditore": "trony"
    }
]'),
('luca verdi', 'IJ789KL', '2025-01-03', 'Napoli', 'Firenze', '[
    {
        "nome":"frigorifero",
        "marca":"samsung",
        "quantity": 5,
        "venditore": "trony"
    },
    {
        "nome":"televisore",
        "marca":"samsung",
        "quantity": 4,
        "venditore": "trony"
    }
]'),
('anna neri', 'MN012OP', '2025-01-04', 'Torino', 'Milano', '[
    {
        "nome":"frigorifero",
        "marca":"samsung",
        "quantity": 5,
        "venditore": "trony"
    },
    {
        "nome":"televisore",
        "marca":"samsung",
        "quantity": 4,
        "venditore": "trony"
    }
]'),
('matteo esposito', 'QR345ST', '2025-01-05', 'Firenze', 'Roma', '[
    {
        "nome":"frigorifero",
        "marca":"samsung",
        "quantity": 5,
        "venditore": "trony"
    },
    {
        "nome":"televisore",
        "marca":"samsung",
        "quantity": 4,
        "venditore": "trony"
    }
]');
