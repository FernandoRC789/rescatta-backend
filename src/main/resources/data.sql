-- ============================================================
-- Datos semilla para el perfil "dev" (H2 en memoria).
-- Se ejecuta automáticamente al arrancar gracias a
-- spring.sql.init.mode=always (ver application.yml).
-- ============================================================

-- ============================================================

-- RAZAS

-- ============================================================



INSERT IGNORE INTO breeds (name, species)

VALUES ('Mestizo', 'PERRO');



INSERT IGNORE INTO breeds (name, species)

VALUES ('Labrador Retriever', 'PERRO');



INSERT IGNORE INTO breeds (name, species)

VALUES ('Pastor Alemán', 'PERRO');



INSERT IGNORE INTO breeds (name, species)

VALUES ('Golden Retriever', 'PERRO');



INSERT IGNORE INTO breeds (name, species)

VALUES ('Chihuahua', 'PERRO');



INSERT IGNORE INTO breeds (name, species)

VALUES ('Criollo Peruano', 'PERRO');



INSERT IGNORE INTO breeds (name, species)

VALUES ('Mestizo', 'GATO');



INSERT IGNORE INTO breeds (name, species)

VALUES ('Siamés', 'GATO');



INSERT IGNORE INTO breeds (name, species)

VALUES ('Persa', 'GATO');



INSERT IGNORE INTO breeds (name, species)

VALUES ('Angora', 'GATO');





-- ============================================================

-- ORGANIZACIONES

-- ============================================================



INSERT IGNORE INTO organizations (name, verified, district)

VALUES ('Refugio Esperanza', true, 'San Isidro, Lima');



INSERT IGNORE INTO organizations (name, verified, district)

VALUES ('Rescate Miraflores', true, 'Miraflores, Lima');



INSERT IGNORE INTO organizations (name, verified, district)

VALUES ('Patitas Callejeras SJL', false, 'San Juan de Lurigancho, Lima');





-- ============================================================

-- MASCOTAS

-- ============================================================



INSERT INTO pets (

    name,

    species,

    breed_id,

    sex,

    age_group,

    age_description,

    size,

    weight_kg,

    adoption_status,

    is_vaccinated,

    is_sterilized,

    is_dewormed,

    health_description,

    description,

    organization_id,

    district,

    latitude,

    longitude,

    adoption_fee_text,

    created_at,

    updated_at

)

SELECT

    'Luna',

    'PERRO',

    b.id,

    'HEMBRA',

    'ADULTO',

    '2 años',

    'MEDIANO',

    18.0,

    'DISPONIBLE',

    true,

    true,

    true,

    'Fue rescatada y recuperada completamente. Actualmente se encuentra sana.',

    'Luna es una perrita cariñosa, activa y muy juguetona. Le encanta correr y jugar con la pelota.',

    o.id,

    'San Isidro, Lima',

    -12.0995,

    -77.0369,

    'Gratis',

    CURRENT_TIMESTAMP,

    CURRENT_TIMESTAMP

FROM breeds b

         JOIN organizations o

WHERE b.name = 'Mestizo'

  AND b.species = 'PERRO'

  AND o.name = 'Refugio Esperanza'

  AND NOT EXISTS (

    SELECT 1 FROM pets p WHERE p.name = 'Luna'

);





INSERT INTO pets (

    name, species, breed_id, sex, age_group, age_description,

    size, weight_kg, adoption_status,

    is_vaccinated, is_sterilized, is_dewormed,

    health_description, description,

    organization_id, district, latitude, longitude,

    adoption_fee_text, created_at, updated_at

)

SELECT

    'Max',

    'PERRO',

    b.id,

    'MACHO',

    'ADULTO',

    '2 años',

    'PEQUENO',

    8.0,

    'EN_PROCESO',

    true,

    false,

    true,

    'Se encuentra saludable y está en proceso de esterilización.',

    'Max es un perrito tranquilo y leal. Al principio puede ser tímido, pero rápidamente toma confianza.',

    o.id,

    'Miraflores, Lima',

    -12.1211,

    -77.0301,

    'Gratis',

    CURRENT_TIMESTAMP,

    CURRENT_TIMESTAMP

FROM breeds b

         JOIN organizations o

WHERE b.name = 'Chihuahua'

  AND b.species = 'PERRO'

  AND o.name = 'Rescate Miraflores'

  AND NOT EXISTS (

    SELECT 1 FROM pets p WHERE p.name = 'Max'

);





INSERT INTO pets (

    name, species, breed_id, sex, age_group, age_description,

    size, weight_kg, adoption_status,

    is_vaccinated, is_sterilized, is_dewormed,

    health_description, description,

    organization_id, district, latitude, longitude,

    adoption_fee_text, created_at, updated_at

)

SELECT

    'Michi',

    'GATO',

    b.id,

    'HEMBRA',

    'CACHORRO',

    '4 meses',

    'PEQUENO',

    2.1,

    'DISPONIBLE',

    true,

    false,

    true,

    'Muy sana y activa.',

    'Michi es una gatita juguetona, curiosa y muy cariñosa. Ideal para una familia.',

    o.id,

    'San Isidro, Lima',

    -12.1005,

    -77.0355,

    'Gratis',

    CURRENT_TIMESTAMP,

    CURRENT_TIMESTAMP

FROM breeds b

         JOIN organizations o

WHERE b.name = 'Siamés'

  AND b.species = 'GATO'

  AND o.name = 'Refugio Esperanza'

  AND NOT EXISTS (

    SELECT 1 FROM pets p WHERE p.name = 'Michi'

);





INSERT INTO pets (

    name, species, breed_id, sex, age_group, age_description,

    size, weight_kg, adoption_status,

    is_vaccinated, is_sterilized, is_dewormed,

    health_description, description,

    organization_id, district, latitude, longitude,

    adoption_fee_text, created_at, updated_at

)

SELECT

    'Rocky',

    'PERRO',

    b.id,

    'MACHO',

    'ADULTO',

    '4 años',

    'GRANDE',

    32.0,

    'DISPONIBLE',

    true,

    true,

    true,

    'Recuperado completamente de una lesión en la pata trasera.',

    'Rocky es tranquilo, protector y cariñoso. Ideal para una casa con espacio.',

    o.id,

    'San Juan de Lurigancho, Lima',

    -11.9998,

    -77.0089,

    'Gratis',

    CURRENT_TIMESTAMP,

    CURRENT_TIMESTAMP

FROM breeds b

         JOIN organizations o

WHERE b.name = 'Pastor Alemán'

  AND b.species = 'PERRO'

  AND o.name = 'Patitas Callejeras SJL'

  AND NOT EXISTS (

    SELECT 1 FROM pets p WHERE p.name = 'Rocky'

);





INSERT INTO pets (

    name, species, breed_id, sex, age_group, age_description,

    size, weight_kg, adoption_status,

    is_vaccinated, is_sterilized, is_dewormed,

    health_description, description,

    organization_id, district, latitude, longitude,

    adoption_fee_text, created_at, updated_at

)

SELECT

    'Nala',

    'GATO',

    b.id,

    'HEMBRA',

    'ADULTO',

    '3 años',

    'MEDIANO',

    4.2,

    'DISPONIBLE',

    true,

    true,

    true,

    'Se encuentra en excelente estado de salud.',

    'Nala es una gata tranquila y elegante. Le gustan los lugares tranquilos y recibir cariño.',

    o.id,

    'Miraflores, Lima',

    -12.1180,

    -77.0290,

    'Gratis',

    CURRENT_TIMESTAMP,

    CURRENT_TIMESTAMP

FROM breeds b

         JOIN organizations o

WHERE b.name = 'Persa'

  AND b.species = 'GATO'

  AND o.name = 'Rescate Miraflores'

  AND NOT EXISTS (

    SELECT 1 FROM pets p WHERE p.name = 'Nala'

);





INSERT INTO pets (

    name, species, breed_id, sex, age_group, age_description,

    size, weight_kg, adoption_status,

    is_vaccinated, is_sterilized, is_dewormed,

    health_description, description,

    organization_id, district, latitude, longitude,

    adoption_fee_text, created_at, updated_at

)

SELECT

    'Toby',

    'PERRO',

    b.id,

    'MACHO',

    'CACHORRO',

    '8 meses',

    'PEQUENO',

    7.5,

    'DISPONIBLE',

    true,

    false,

    true,

    'Cachorro sano y con sus vacunas al día.',

    'Toby es energético, sociable y le encanta jugar con otros perros.',

    o.id,

    'San Isidro, Lima',

    -12.0970,

    -77.0340,

    'Gratis',

    CURRENT_TIMESTAMP,

    CURRENT_TIMESTAMP

FROM breeds b

         JOIN organizations o

WHERE b.name = 'Labrador Retriever'

  AND b.species = 'PERRO'

  AND o.name = 'Refugio Esperanza'

  AND NOT EXISTS (

    SELECT 1 FROM pets p WHERE p.name = 'Toby'

);





INSERT INTO pets (

    name, species, breed_id, sex, age_group, age_description,

    size, weight_kg, adoption_status,

    is_vaccinated, is_sterilized, is_dewormed,

    health_description, description,

    organization_id, district, latitude, longitude,

    adoption_fee_text, created_at, updated_at

)

SELECT

    'Simba',

    'GATO',

    b.id,

    'MACHO',

    'CACHORRO',

    '7 meses',

    'PEQUENO',

    3.0,

    'DISPONIBLE',

    true,

    false,

    true,

    'Sano, activo y con controles veterinarios.',

    'Simba es curioso y juguetón. Disfruta explorar y dormir en lugares altos.',

    o.id,

    'San Isidro, Lima',

    -12.1020,

    -77.0330,

    'Gratis',

    CURRENT_TIMESTAMP,

    CURRENT_TIMESTAMP

FROM breeds b

         JOIN organizations o

WHERE b.name = 'Mestizo'

  AND b.species = 'GATO'

  AND o.name = 'Refugio Esperanza'

  AND NOT EXISTS (

    SELECT 1 FROM pets p WHERE p.name = 'Simba'

);





INSERT INTO pets (

    name, species, breed_id, sex, age_group, age_description,

    size, weight_kg, adoption_status,

    is_vaccinated, is_sterilized, is_dewormed,

    health_description, description,

    organization_id, district, latitude, longitude,

    adoption_fee_text, created_at, updated_at

)

SELECT

    'Bruno',

    'PERRO',

    b.id,

    'MACHO',

    'ADULTO',

    '3 años',

    'GRANDE',

    28.0,

    'DISPONIBLE',

    true,

    true,

    true,

    'Muy saludable y con controles veterinarios al día.',

    'Bruno es amigable, protector y muy cariñoso con las personas.',

    o.id,

    'Miraflores, Lima',

    -12.1200,

    -77.0310,

    'Gratis',

    CURRENT_TIMESTAMP,

    CURRENT_TIMESTAMP

FROM breeds b

         JOIN organizations o

WHERE b.name = 'Golden Retriever'

  AND b.species = 'PERRO'

  AND o.name = 'Rescate Miraflores'

  AND NOT EXISTS (

    SELECT 1 FROM pets p WHERE p.name = 'Bruno'

);





INSERT INTO pets (

    name, species, breed_id, sex, age_group, age_description,

    size, weight_kg, adoption_status,

    is_vaccinated, is_sterilized, is_dewormed,

    health_description, description,

    organization_id, district, latitude, longitude,

    adoption_fee_text, created_at, updated_at

)

SELECT

    'Kira',

    'GATO',

    b.id,

    'HEMBRA',

    'ADULTO',

    '2 años',

    'PEQUENO',

    3.5,

    'DISPONIBLE',

    true,

    true,

    true,

    'Se encuentra completamente sana.',

    'Kira es una gatita dulce, tranquila y muy cariñosa.',

    o.id,

    'San Isidro, Lima',

    -12.1010,

    -77.0370,

    'Gratis',

    CURRENT_TIMESTAMP,

    CURRENT_TIMESTAMP

FROM breeds b

         JOIN organizations o

WHERE b.name = 'Angora'

  AND b.species = 'GATO'

  AND o.name = 'Refugio Esperanza'

  AND NOT EXISTS (

    SELECT 1 FROM pets p WHERE p.name = 'Kira'

);





INSERT INTO pets (

    name, species, breed_id, sex, age_group, age_description,

    size, weight_kg, adoption_status,

    is_vaccinated, is_sterilized, is_dewormed,

    health_description, description,

    organization_id, district, latitude, longitude,

    adoption_fee_text, created_at, updated_at

)

SELECT

    'Maya',

    'PERRO',

    b.id,

    'HEMBRA',

    'SENIOR',

    '7 años',

    'MEDIANO',

    16.0,

    'DISPONIBLE',

    true,

    true,

    true,

    'Se encuentra saludable y recibe controles veterinarios.',

    'Maya es una perrita tranquila, noble y muy cariñosa. Busca una familia que le dé mucho amor.',

    o.id,

    'San Isidro, Lima',

    -12.0960,

    -77.0370,

    'Gratis',

    CURRENT_TIMESTAMP,

    CURRENT_TIMESTAMP

FROM breeds b

         JOIN organizations o

WHERE b.name = 'Criollo Peruano'

  AND b.species = 'PERRO'

  AND o.name = 'Refugio Esperanza'

  AND NOT EXISTS (

    SELECT 1 FROM pets p WHERE p.name = 'Maya'

);





-- ============================================================

-- FOTOS

-- ============================================================



INSERT INTO pet_photos (pet_id, url, photo_order)

SELECT id,

       'https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=800&q=80',

       0

FROM pets

WHERE name = 'Luna'

  AND NOT EXISTS (

    SELECT 1 FROM pet_photos pp WHERE pp.pet_id = pets.id

);





INSERT INTO pet_photos (pet_id, url, photo_order)

SELECT id,

       'https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=800&q=80',

       0

FROM pets

WHERE name = 'Max'

  AND NOT EXISTS (

    SELECT 1 FROM pet_photos pp WHERE pp.pet_id = pets.id

);





INSERT INTO pet_photos (pet_id, url, photo_order)

SELECT id,

       'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=800&q=80',

       0

FROM pets

WHERE name = 'Michi'

  AND NOT EXISTS (

    SELECT 1 FROM pet_photos pp WHERE pp.pet_id = pets.id

);





INSERT INTO pet_photos (pet_id, url, photo_order)

SELECT id,

       'https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&w=800&q=80',

       0

FROM pets

WHERE name = 'Rocky'

  AND NOT EXISTS (

    SELECT 1 FROM pet_photos pp WHERE pp.pet_id = pets.id

);





INSERT INTO pet_photos (pet_id, url, photo_order)

SELECT id,

       'https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&w=800&q=80',

       0

FROM pets

WHERE name = 'Nala'

  AND NOT EXISTS (

    SELECT 1 FROM pet_photos pp WHERE pp.pet_id = pets.id

);





INSERT INTO pet_photos (pet_id, url, photo_order)

SELECT id,

       'https://images.unsplash.com/photo-1561037404-61cd46aa615b?auto=format&fit=crop&w=800&q=80',

       0

FROM pets

WHERE name = 'Toby'

  AND NOT EXISTS (

    SELECT 1 FROM pet_photos pp WHERE pp.pet_id = pets.id

);





INSERT INTO pet_photos (pet_id, url, photo_order)

SELECT id,

       'https://images.unsplash.com/photo-1573865526739-10659fec78a5?auto=format&fit=crop&w=800&q=80',

       0

FROM pets

WHERE name = 'Simba'

  AND NOT EXISTS (

    SELECT 1 FROM pet_photos pp WHERE pp.pet_id = pets.id

);





INSERT INTO pet_photos (pet_id, url, photo_order)

SELECT id,

       'https://images.unsplash.com/photo-1558788353-f76d92427f16?auto=format&fit=crop&w=800&q=80',

       0

FROM pets

WHERE name = 'Bruno'

  AND NOT EXISTS (

    SELECT 1 FROM pet_photos pp WHERE pp.pet_id = pets.id

);





INSERT INTO pet_photos (pet_id, url, photo_order)

SELECT id,

       'https://images.unsplash.com/photo-1518791841217-8f162f1e1131?auto=format&fit=crop&w=800&q=80',

       0

FROM pets

WHERE name = 'Kira'

  AND NOT EXISTS (

    SELECT 1 FROM pet_photos pp WHERE pp.pet_id = pets.id

);





INSERT INTO pet_photos (pet_id, url, photo_order)

SELECT id,

       'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&w=800&q=80',

       0

FROM pets

WHERE name = 'Maya'

  AND NOT EXISTS (

    SELECT 1 FROM pet_photos pp WHERE pp.pet_id = pets.id

);