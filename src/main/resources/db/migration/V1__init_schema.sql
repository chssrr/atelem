CREATE TABLE cabinets (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    serial_number VARCHAR(20),
    manufacturer VARCHAR(30),
    purchase_date DATE,
    status VARCHAR(30),
    location VARCHAR(50)
);

CREATE TABLE devices (
    id BIGSERIAL PRIMARY KEY,
    serial_number VARCHAR(20) NOT NULL,
    manufacturer VARCHAR(30),
    purchase_date DATE,
    status VARCHAR(30),
    cabinet_id BIGINT,
    CONSTRAINT fk_cabinet FOREIGN KEY (cabinet_id) REFERENCES cabinets(id)
);
