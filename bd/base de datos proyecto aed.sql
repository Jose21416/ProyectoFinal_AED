create database EduTek;
use EduTek;

create table Alumno(
	idAlumno int auto_increment primary key,
    codAlumno int unique,
    nombre varchar(50) not null,
    apellidos varchar(50) not null,
    dni varchar(50) not null unique,
    edad int not null,
    celular int not null,
    estado int not null
);

create table Curso(
	idCurso int auto_increment primary key,
    codCurso int unique,
    asignatura varchar(50) not null,
    ciclo int not null,
    creditos int not null,
    horas int not null
);

create table Matricula(
	idMatricula int auto_increment primary key,
    codMatricula int unique,
    fecha date not null,
    hora time not null,
    idAlumno int,
    idCurso int,
    foreign key (idAlumno) references Alumno(idAlumno),
    foreign key (idCurso) references Curso(idCurso)
);

create table Retiro(
	idRetiro int auto_increment primary key,
    codRetiro int unique,
    fecha date not null,
    hora time not null,
    idMatricula int,
    foreign key (idMatricula) references Matricula(idMatricula)
);


select a.idAlumno,a.nombre,a.apellidos,a.dni,a.edad,a.celular,a.estado
from alumno a left join matricula m on a.idAlumno=m.idAlumno 
where a.estado=0;
                     
SELECT COUNT(CASE WHEN a.estado=1 THEN 1 END) AS matriculados,
		COUNT(CASE WHEN a.estado=0 THEN 1 END) AS registrados,
		COUNT(CASE WHEN a.estado=2 THEN 1 END) AS retirados
		FROM Alumno a
		LEFT JOIN Matricula m ON a.idAlumno = m.idAlumno
		LEFT JOIN Retiro r ON m.idMatricula = r.idMatricula;
        
        
----------------------------- TRIGGERS PARA LOS NÚMEROS CORRELATIVOS DE LAS DIFERENTES TABLAS ---------------------------------------

-- ALUMNO
DELIMITER $$

CREATE TRIGGER trg_codAlumno
BEFORE INSERT ON Alumno
FOR EACH ROW
BEGIN
    IF NEW.codAlumno IS NULL OR NEW.codAlumno = 0 THEN
        SET NEW.codAlumno = CONCAT(
            YEAR(NOW()),
            LPAD(
                (SELECT IFNULL(MAX(CAST(SUBSTRING(codAlumno, 5, 5) AS UNSIGNED)), 10000) + 1 
                 FROM Alumno),
                5,
                '0'
            )
        );
    END IF;
END$$

DELIMITER ;


-- CURSO

DELIMITER $$

CREATE TRIGGER trg_codCurso
BEFORE INSERT ON Curso
FOR EACH ROW
BEGIN
    IF NEW.codCurso IS NULL OR NEW.codCurso = 0 THEN
        SET NEW.codCurso = (
            SELECT IFNULL(MAX(codCurso), 300000) + 1
            FROM Curso
        );
    END IF;
END$$

DELIMITER ;


-- MATRICULA
DELIMITER $$

CREATE TRIGGER trg_codMatricula
BEFORE INSERT ON Matricula
FOR EACH ROW
BEGIN
    IF NEW.codMatricula IS NULL OR NEW.codMatricula = 0 THEN
        SET NEW.codMatricula = (
            SELECT IFNULL(MAX(codMatricula), 100000) + 1
            FROM Matricula
        );
    END IF;
END$$

DELIMITER ;



-- RETIRO

DELIMITER $$

CREATE TRIGGER trg_codRetiro
BEFORE INSERT ON Retiro
FOR EACH ROW
BEGIN
    IF NEW.codRetiro IS NULL OR NEW.codRetiro = 0 THEN
        SET NEW.codRetiro = (
            SELECT IFNULL(MAX(codRetiro), 200000) + 1
            FROM Retiro
        );
    END IF;
END$$

DELIMITER ;



INSERT INTO Alumno (nombre, apellidos, dni, edad, celular, estado) VALUES
('Carlos', 'Gómez Ruiz', '42157894', 20, 912345001, 0),
('María', 'López Díaz', '45812967', 22, 912345002, 0),
('Juan', 'Torres Vega', '76341259', 19, 912345003, 0),
('Lucía', 'Fernández León', '50231874', 21, 912345004, 0),
('Pedro', 'Ramos Silva', '74821590', 23, 912345005, 0),
('Ana', 'Navarro Cruz', '61729845', 20, 912345006, 0),
('Luis', 'Castillo Soto', '84572136', 18, 912345007, 0),
('Elena', 'Paredes Mora', '53172468', 24, 912345008, 0),
('Diego', 'Mendoza Solís', '72915483', 22, 912345009, 0),
('Rosa', 'Aguilar Campos', '68251743', 21, 912345010, 0),
('Miguel', 'Ortega Luna', '76420158', 20, 912345011, 0),
('Sofía', 'Reyes Bravo', '53821970', 19, 912345012, 0),
('Javier', 'Cabrera Peña', '45789162', 23, 912345013, 0),
('Patricia', 'Salas Núñez', '81245796', 18, 912345014, 0),
('Hugo', 'García Torres', '64129873', 21, 912345015, 0),
('Camila', 'Cortez Ramos', '78216439', 22, 912345016, 0),
('Raúl', 'Espinoza Vera', '52478361', 20, 912345017, 0),
('Valeria', 'Zapata Ríos', '71389264', 19, 912345018, 0),
('Andrés', 'Montoya Díaz', '67824513', 24, 912345019, 0),
('Daniela', 'Suárez Rojas', '59213847', 23, 912345020, 0),
('Fernando', 'Acosta Prado', '76489321', 22, 912345021, 0),
('Paula', 'Medina Vargas', '43287159', 20, 912345022, 0),
('Sebastián', 'Iglesias Solano', '82476190', 18, 912345023, 0),
('Natalia', 'Matos Rivera', '56174829', 21, 912345024, 0),
('Óscar', 'Rivas Huamán', '70521864', 20, 912345025, 0),
('Liliana', 'Peralta Guerra', '63417820', 22, 912345026, 0),
('Iván', 'Romero Páez', '75846291', 23, 912345027, 0),
('Carolina', 'Santos Oliva', '51483927', 19, 912345028, 0),
('Gustavo', 'Palacios Peña', '83214765', 24, 912345029, 0),
('Mónica', 'Herrera Lozano', '67391542', 20, 912345030, 0);
