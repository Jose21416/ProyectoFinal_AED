create database EduTek;
use EduTek;

create table Alumno(
	idAlumno int auto_increment primary key,
    nombre varchar(50) not null,
    apellidos varchar(50) not null,
    dni varchar(50) not null,
    edad int not null,
    celular int not null,
    estado int not null
);

create table Curso(
	idCurso int auto_increment primary key,
    asignatura varchar(50) not null,
    ciclo int not null,
    creditos int not null,
    horas int not null
);

create table Matricula(
	idMatricula int auto_increment primary key,
    fecha date not null,
    hora time not null,
    idAlumno int,
    idCurso int,
    foreign key (idAlumno) references Alumno(idAlumno),
    foreign key (idCurso) references Curso(idCurso)
) auto_increment = 100001;

create table Retiro(
	idRetiro int auto_increment primary key,
    fecha date not null,
    hora time not null,
    idMatricula int,
    foreign key (idMatricula) references Matricula(idMatricula)
) auto_increment = 200001;

-- =========================================================
-- INSERTS MODIFICADOS CON CONTEXTO PERUANO Y ESTADOS LOGICOS
-- =========================================================

INSERT INTO Alumno (nombre, apellidos, dni, edad, celular, estado) VALUES
-- ESTADO 1: MATRICULADOS ACTIVOS
('Juan Carlos', 'Pérez Gómez', '71234567', 20, '987654321', 1),
('María Elena', 'López Ruiz', '72345678', 22, '912345678', 1),
('Pedro Luis', 'Martínez Soto', '73456789', 19, '945678123', 1),
('Ana Sofía', 'García Fernández', '74567890', 21, '923456789', 1),
('Carla Daniela', 'Ramírez Ortiz', '75678901', 20, '955123456', 1),
('Diego Armando', 'Sánchez Vega', '76789012', 21, '966234567', 1),
('Valeria Paz', 'Castro Mendoza', '77890123', 19, '977345678', 1),
('Javier Andrés', 'Herrera Luna', '78901234', 22, '988456789', 1),
('Sofía Camila', 'Rojas Paredes', '79012345', 18, '999567890', 1),
('Lucía Fernanda', 'Vargas Cruz', '70123456', 20, '922789012', 1),
('Renato José', 'Molina Reyes', '71123456', 21, '933890123', 1),
('Isabella Marie', 'Navarro Silva', '72123456', 19, '944901234', 1),

-- ESTADO 2: RETIRADOS (Tenían matrícula, pero luego se retiraron)
('Luis Miguel', 'Torres Vega', '73123456', 23, '934567890', 2),
('Mateo Gabriel', 'Flores Díaz', '74123456', 23, '911678901', 2),
('Tomás Eduardo', 'Gómez Salazar', '75123456', 24, '955012345', 2),

-- ESTADO 0: REGISTRADOS (Sin matrícula aún)
('Alejandro Manuel', 'Reyes Huamán', '76123456', 25, '980123456', 0),
('Gabriela Belén', 'Chávez Solís', '77123456', 20, '990123456', 0);


INSERT INTO Curso (asignatura, ciclo, creditos, horas) VALUES
('Matemáticas I', 1, 6, 90),
('Programación Básica', 1, 5, 75),
('Bases de Datos', 2, 6, 90),
('Inglés Técnico', 1, 4, 60),
('Física Aplicada', 2, 5, 80),
('Álgebra Lineal', 2, 6, 90),
('Estructuras de Datos', 2, 6, 90),
('Redes de Computadoras', 3, 5, 80),
('Desarrollo Web', 2, 5, 75),
('Estadística Aplicada', 3, 4, 60),
('Inteligencia Artificial', 4, 6, 90),
('Cálculo II', 2, 6, 90),
('Sistemas Operativos', 3, 5, 80),
('Diseño de Interfaces', 3, 4, 60),
('Ética Profesional', 4, 3, 45);

-- Se insertan las matrículas solo para los alumnos con estado 1 y 2
INSERT INTO Matricula (fecha, hora, idAlumno, idCurso) VALUES
('2025-03-15', '09:30:00', 1, 1),  -- Activo
('2025-03-15', '10:15:00', 2, 2),  -- Activo
('2025-03-16', '08:45:00', 3, 3),  -- Activo
('2025-03-16', '11:00:00', 4, 1),  -- Activo
('2025-03-17', '14:20:00', 5, 2),  -- Retirado (ID 5)
('2025-03-17', '15:10:00', 6, 4),  -- Activo
('2025-03-18', '09:00:00', 7, 5),  -- Activo
('2025-03-19', '09:15:00', 8, 3),  -- Activo
('2025-03-19', '10:30:00', 9, 6),  -- Activo
('2025-03-20', '08:00:00', 10, 1), -- Activo
('2025-03-20', '11:45:00', 11, 4), -- Activo
('2025-03-21', '13:20:00', 12, 2), -- Activo
('2025-03-21', '14:10:00', 13, 5), -- Activo
('2025-03-22', '09:30:00', 14, 7), -- Activo
('2025-03-22', '15:00:00', 15, 8); -- Retirado (ID 15)


-- Se insertan los retiros solo para los alumnos con estado 2 (IDs 5, 14, 15)
INSERT INTO Retiro (fecha, hora, idMatricula) VALUES
('2025-04-10', '16:30:00', 100005),  -- Luis Miguel (ID Alumno 5)
('2025-04-20', '09:10:00', 100014),  -- Mateo Gabriel (ID Alumno 14)
('2025-04-24', '15:45:00', 100015);  -- Tomás Eduardo (ID Alumno 15)



select a.idAlumno,a.nombre,a.apellidos,a.dni,a.edad,a.celular,a.estado
from alumno a left join matricula m on a.idAlumno=m.idAlumno 
where a.estado=0;
                     
SELECT COUNT(CASE WHEN a.estado=1 THEN 1 END) AS matriculados,
		COUNT(CASE WHEN a.estado=0 THEN 1 END) AS registrados,
		COUNT(CASE WHEN a.estado=2 THEN 1 END) AS retirados
		FROM Alumno a
		LEFT JOIN Matricula m ON a.idAlumno = m.idAlumno
		LEFT JOIN Retiro r ON m.idMatricula = r.idMatricula;