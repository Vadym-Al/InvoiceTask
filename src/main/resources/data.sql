INSERT INTO invoices (id, customer_name, total_amount, paid_amount, status, created_at)
VALUES (
           '11111111-1111-1111-1111-111111111111',
           'John Doe',
           100.00,
           40.00,
           'PARTIALLY_PAID',
           NOW()
       );

INSERT INTO line_items (id, invoice_id, description, price, quantity)
VALUES
    (
        '22222222-2222-2222-2222-222222222221',
        '11111111-1111-1111-1111-111111111111',
        'Laptop Stand',
        50.00,
        2
    );

INSERT INTO payments (id, invoice_id, amount, type, created_at)
VALUES
    (
        '33333333-3333-3333-3333-333333333331',
        '11111111-1111-1111-1111-111111111111',
        40.00,
        'PAYMENT',
        NOW()
    );

-------------------------------------------------------

INSERT INTO invoices (id, customer_name, total_amount, paid_amount, status, created_at)
VALUES (
           '44444444-4444-4444-4444-444444444444',
           'Alice Smith',
           200.00,
           0.00,
           'ISSUED',
           NOW()
       );

INSERT INTO line_items (id, invoice_id, description, price, quantity)
VALUES
    (
        '55555555-5555-5555-5555-555555555551',
        '44444444-4444-4444-4444-444444444444',
        'Office Chair',
        100.00,
        2
    );