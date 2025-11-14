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