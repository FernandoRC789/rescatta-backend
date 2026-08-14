-- ============================================================
-- Datos semilla para el perfil "dev" (H2 en memoria).
-- Se ejecuta automáticamente al arrancar gracias a
-- spring.sql.init.mode=always (ver application.yml).
-- ============================================================

-- Razas de perro
INSERT INTO breeds (name, species) VALUES ('Mestizo', 'PERRO');
INSERT INTO breeds (name, species) VALUES ('Labrador Retriever', 'PERRO');
INSERT INTO breeds (name, species) VALUES ('Pastor Alemán', 'PERRO');
INSERT INTO breeds (name, species) VALUES ('Golden Retriever', 'PERRO');
INSERT INTO breeds (name, species) VALUES ('Chihuahua', 'PERRO');
INSERT INTO breeds (name, species) VALUES ('Criollo Peruano', 'PERRO');

-- Razas de gato
INSERT INTO breeds (name, species) VALUES ('Mestizo', 'GATO');
INSERT INTO breeds (name, species) VALUES ('Siamés', 'GATO');
INSERT INTO breeds (name, species) VALUES ('Persa', 'GATO');
INSERT INTO breeds (name, species) VALUES ('Angora', 'GATO');

-- Organizaciones (refugios/rescatistas) demo
INSERT INTO organizations (name, verified, district) VALUES ('Refugio Esperanza', true, 'San Isidro, Lima');
INSERT INTO organizations (name, verified, district) VALUES ('Rescate Miraflores', true, 'Miraflores, Lima');
INSERT INTO organizations (name, verified, district) VALUES ('Patitas Callejeras SJL', false, 'San Juan de Lurigancho, Lima');

-- Mascotas de ejemplo en el catálogo (coordenadas reales de Lima, para probar "cerca de mí")
INSERT INTO pets (name, species, breed_id, sex, age_group, age_description, size, weight_kg,
                   adoption_status, is_vaccinated, is_sterilized, is_dewormed,
                   health_description, description, organization_id, district,
                   latitude, longitude, adoption_fee_text, created_at, updated_at)
VALUES ('Luna', 'PERRO', 1, 'HEMBRA', 'ADULTO', '2 años', 'MEDIANO', 18.0,
        'DISPONIBLE', true, true, true,
        'Fue tratada por desnutrición leve al momento del rescate; hoy está completamente sana.',
        'Luna es una perrita muy cariñosa y llena de energía. Fue rescatada de las afueras de la ciudad. Le encanta correr por el parque y jugar a la pelota.',
        1, 'San Isidro, Lima', -12.0995, -77.0369, 'Gratis', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO pets (name, species, breed_id, sex, age_group, age_description, size, weight_kg,
                   adoption_status, is_vaccinated, is_sterilized, is_dewormed,
                   health_description, description, organization_id, district,
                   latitude, longitude, adoption_fee_text, created_at, updated_at)
VALUES ('Max', 'PERRO', 1, 'MACHO', 'ADULTO', '2 años', 'PEQUENO', 8.0,
        'EN_PROCESO', true, false, true,
        'En proceso de esterilización programada para la próxima semana.',
        'Max es un poco tímido al principio, pero muy leal una vez que gana confianza.',
        2, 'Miraflores, Lima', -12.1211, -77.0301, 'Gratis', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO pets (name, species, breed_id, sex, age_group, age_description, size, weight_kg,
                   adoption_status, is_vaccinated, is_sterilized, is_dewormed,
                   health_description, description, organization_id, district,
                   latitude, longitude, adoption_fee_text, created_at, updated_at)
VALUES ('Michi', 'GATO', 7, 'HEMBRA', 'CACHORRO', '4 meses', 'PEQUENO', 2.1,
        'DISPONIBLE', true, false, true,
        'Muy sana, solo falta esterilizar cuando alcance la edad mínima recomendada.',
        'Michi es juguetona y curiosa, ideal para una familia con niños.',
        1, 'San Isidro, Lima', -12.1005, -77.0355, 'Gratis', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO pets (name, species, breed_id, sex, age_group, age_description, size, weight_kg,
                   adoption_status, is_vaccinated, is_sterilized, is_dewormed,
                   health_description, description, organization_id, district,
                   latitude, longitude, adoption_fee_text, created_at, updated_at)
VALUES ('Rocky', 'PERRO', 3, 'MACHO', 'ADULTO', '4 años', 'GRANDE', 32.0,
        'DISPONIBLE', true, true, true,
        'Recuperado completamente de una fractura en la pata trasera derecha.',
        'Rocky es protector y tranquilo, ideal para una casa con patio.',
        3, 'San Juan de Lurigancho, Lima', -11.9998, -77.0089, 'Gratis', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Comportamiento (tags) de cada mascota
-- Mascota 1
INSERT INTO pet_temperament_tags (pet_id, tag, tag_order) VALUES (1, 'SOCIABLE', 0);
INSERT INTO pet_temperament_tags (pet_id, tag, tag_order) VALUES (1, 'ACTIVO', 1);
INSERT INTO pet_temperament_tags (pet_id, tag, tag_order) VALUES (1, 'APTO_NINOS', 2);
INSERT INTO pet_temperament_tags (pet_id, tag, tag_order) VALUES (1, 'APTO_PERROS', 3);

-- Mascota 2
INSERT INTO pet_temperament_tags (pet_id, tag, tag_order) VALUES (2, 'TIMIDO', 0);

-- Mascota 3
INSERT INTO pet_temperament_tags (pet_id, tag, tag_order) VALUES (3, 'SOCIABLE', 0);
INSERT INTO pet_temperament_tags (pet_id, tag, tag_order) VALUES (3, 'ACTIVO', 1);

-- Mascota 4
INSERT INTO pet_temperament_tags (pet_id, tag, tag_order) VALUES (4, 'PROTECTOR', 0);
INSERT INTO pet_temperament_tags (pet_id, tag, tag_order) VALUES (4, 'TRANQUILO', 1);

-- Fotos placeholder (reemplazar por URLs reales cuando se conecte el storage definitivo)
INSERT INTO pet_photos (pet_id, url, photo_order) VALUES (1, '/uploads/seed/luna_1.jpg', 0);
INSERT INTO pet_photos (pet_id, url, photo_order) VALUES (2, '/uploads/seed/max_1.jpg', 0);
INSERT INTO pet_photos (pet_id, url, photo_order) VALUES (3, '/uploads/seed/michi_1.jpg', 0);
INSERT INTO pet_photos (pet_id, url, photo_order) VALUES (4, '/uploads/seed/rocky_1.jpg', 0);

-- Un par de reportes de ejemplo (para probar /api/v1/reports/nearby y /api/v1/home/citizen-summary)
INSERT INTO animal_reports (species, age_group, breed_id, animal_condition, description, latitude, longitude,
                             address, status, reporter_uid, reporter_can_stay, created_at, updated_at)
VALUES ('PERRO', null, null, 'ATROPELLADO', 'Perro mestizo herido en la pata trasera, cerca del cruce principal.',
        -12.0980, -77.0350, 'Av. Caracas con Calle 45', 'PENDIENTE', 'demo-uid-ciudadano-1', true,
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 10 MINUTE), DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 10 MINUTE));

INSERT INTO animal_reports (species, age_group, breed_id, animal_condition, description, latitude, longitude,
                             address, status, reporter_uid, reporter_can_stay, created_at, updated_at)
VALUES ('GATO', null, null, 'SANO', 'Gato blanco y gris escondido bajo un auto estacionado.',
        -12.1015, -77.0340, 'Parque Nacional', 'EN_PROCESO', 'demo-uid-ciudadano-2', false,
        DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 2 HOUR), DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 2 HOUR));

INSERT INTO report_photos (report_id, url, photo_order) VALUES (1, '/uploads/seed/reporte_1.jpg', 0);
INSERT INTO report_photos (report_id, url, photo_order) VALUES (2, '/uploads/seed/reporte_2.jpg', 0);
