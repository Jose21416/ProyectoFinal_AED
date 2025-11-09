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

INSERT INTO Alumno (nombre, apellidos, dni, edad, celular, estado) VALUES
('Juan Carlos', 'Pérez Gómez', '12345678A', 20, 987654321, 1),
('María Elena', 'López Ruiz', '87654321B', 22, 912345678, 1),
('Pedro Luis', 'Martínez Soto', '45678912C', 19, 945678123, 1),
('Ana Sofía', 'García Fernández', '78912345D', 21, 923456789, 1),
('Luis Miguel', 'Torres Vega', '32165498E', 23, 934567890, 0),
('Carla Daniela', 'Ramírez Ortiz', '11223344F', 20, 955123456, 1),
('Diego Armando', 'Sánchez Vega', '22334455G', 21, 966234567, 1),
('Valeria Paz', 'Castro Mendoza', '33445566H', 19, 977345678, 1),
('Javier Andrés', 'Herrera Luna', '44556677I', 22, 988456789, 1),
('Sofía Camila', 'Rojas Paredes', '55667788J', 18, 999567890, 1),
('Mateo Gabriel', 'Flores Díaz', '66778899K', 23, 911678901, 0),
('Lucía Fernanda', 'Vargas Cruz', '77889900L', 20, 922789012, 1),
('Renato José', 'Molina Reyes', '88990011M', 21, 933890123, 1),
('Isabella Marie', 'Navarro Silva', '99001122N', 19, 944901234, 1),
('Tomás Eduardo', 'Gómez Salazar', '00112233O', 24, 955012345, 1);


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

INSERT INTO Matricula (fecha, hora, idAlumno, idCurso) VALUES
('2025-03-15', '09:30:00', 1, 1),
('2025-03-15', '10:15:00', 1, 2),
('2025-03-16', '08:45:00', 2, 3),
('2025-03-16', '11:00:00', 3, 1),
('2025-03-17', '14:20:00', 4, 2),
('2025-03-17', '15:10:00', 4, 4),
('2025-03-18', '09:00:00', 2, 5),
('2025-03-19', '09:15:00', 6, 3),
('2025-03-19', '10:30:00', 6, 6),
('2025-03-20', '08:00:00', 7, 1),
('2025-03-20', '11:45:00', 7, 4),
('2025-03-21', '13:20:00', 8, 2),
('2025-03-21', '14:10:00', 8, 5),
('2025-03-22', '09:30:00', 9, 7),
('2025-03-22', '15:00:00', 9, 8),
('2025-03-23', '10:00:00', 10, 9),
('2025-03-23', '16:20:00', 10, 10),
('2025-03-24', '08:45:00', 11, 11),
('2025-03-24', '12:30:00', 11, 12),
('2025-03-25', '14:00:00', 12, 13),
('2025-03-25', '15:30:00', 13, 14),
('2025-03-26', '11:15:00', 14, 15);

INSERT INTO Retiro (fecha, hora, idMatricula) VALUES
('2025-04-10', '16:30:00', 100001),  -- Juan retira Matemáticas I
('2025-04-12', '10:20:00', 100005),  -- Ana retira Programación Básica
('2025-04-15', '13:45:00', 100003),  -- María retira Bases de Datos
('2025-04-18', '10:45:00', 100008),  -- Carla retira Bases de Datos
('2025-04-19', '14:20:00', 100010),  -- Diego retira Programación Básica
('2025-04-20', '09:10:00', 100012),  -- Valeria retira Estructuras de Datos
('2025-04-21', '16:00:00', 100015),  -- Javier retira Desarrollo Web
('2025-04-22', '11:30:00', 100018),  -- Sofía retira Cálculo II
('2025-04-23', '13:15:00', 100020),  -- Mateo retira Sistemas Operativos
('2025-04-24', '15:45:00', 100022);  -- Lucía retira Inteligencia Artificial

