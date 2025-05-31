--
-- PostgreSQL database dump
--

-- Dumped from database version 17.2
-- Dumped by pg_dump version 17.2

-- Started on 2025-05-21 14:03:34 +03

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 220 (class 1259 OID 16685)
-- Name: admins; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.admins (
    username character varying(50) NOT NULL,
    password character varying(100) NOT NULL,
    dealer_id integer,
    name character varying(100) NOT NULL,
    age integer NOT NULL,
    phone character varying(10) NOT NULL
);


ALTER TABLE public.admins OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16701)
-- Name: cars; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cars (
    id integer NOT NULL,
    brand character varying(50) NOT NULL,
    model character varying(50) NOT NULL,
    year integer NOT NULL,
    package_type character varying(50) NOT NULL,
    price numeric(10,2) NOT NULL,
    dealer integer,
    status character varying(20) DEFAULT 'Stokta'::character varying,
    CONSTRAINT cars_status_check CHECK (((status)::text = ANY ((ARRAY['Stokta'::character varying, 'Satıldı'::character varying, 'Gösterimde'::character varying, 'Test Sürüşünde'::character varying])::text[])))
);


ALTER TABLE public.cars OWNER TO postgres;

--
-- TOC entry 221 (class 1259 OID 16700)
-- Name: cars_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.cars_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.cars_id_seq OWNER TO postgres;

--
-- TOC entry 3707 (class 0 OID 0)
-- Dependencies: 221
-- Name: cars_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.cars_id_seq OWNED BY public.cars.id;


--
-- TOC entry 219 (class 1259 OID 16680)
-- Name: customers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customers (
    username character varying(50) NOT NULL,
    password character varying(100) NOT NULL,
    name character varying(100) NOT NULL,
    age integer NOT NULL,
    phone character varying(10) NOT NULL,
    registration_date timestamp without time zone
);


ALTER TABLE public.customers OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 16672)
-- Name: dealers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dealers (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    address text NOT NULL,
    phone character varying(10) NOT NULL,
    email character varying(100) NOT NULL,
    admin_id character varying(50)
);


ALTER TABLE public.dealers OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 16671)
-- Name: dealers_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.dealers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.dealers_id_seq OWNER TO postgres;

--
-- TOC entry 3708 (class 0 OID 0)
-- Dependencies: 217
-- Name: dealers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.dealers_id_seq OWNED BY public.dealers.id;


--
-- TOC entry 228 (class 1259 OID 16763)
-- Name: orders; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.orders (
    id integer NOT NULL,
    car_id integer,
    customer_id character varying(50),
    dealer_id integer,
    order_date timestamp without time zone NOT NULL,
    status character varying(20) DEFAULT 'Bekliyor'::character varying,
    CONSTRAINT orders_status_check CHECK (((status)::text = ANY ((ARRAY['Bekliyor'::character varying, 'Reddedildi'::character varying, 'Onaylandı'::character varying])::text[])))
);


ALTER TABLE public.orders OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 16762)
-- Name: orders_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.orders_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.orders_id_seq OWNER TO postgres;

--
-- TOC entry 3709 (class 0 OID 0)
-- Dependencies: 227
-- Name: orders_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.orders_id_seq OWNED BY public.orders.id;


--
-- TOC entry 224 (class 1259 OID 16715)
-- Name: price_offers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.price_offers (
    id integer NOT NULL,
    car_id integer,
    customer_id character varying(50),
    dealer_id integer,
    offer_price numeric(10,2) NOT NULL,
    offer_date timestamp without time zone NOT NULL,
    status character varying(20) DEFAULT 'Bekliyor'::character varying,
    CONSTRAINT price_offers_status_check CHECK (((status)::text = ANY ((ARRAY['Bekliyor'::character varying, 'Reddedildi'::character varying, 'Onaylandı'::character varying])::text[])))
);


ALTER TABLE public.price_offers OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 16714)
-- Name: price_offers_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.price_offers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.price_offers_id_seq OWNER TO postgres;

--
-- TOC entry 3710 (class 0 OID 0)
-- Dependencies: 223
-- Name: price_offers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.price_offers_id_seq OWNED BY public.price_offers.id;


--
-- TOC entry 230 (class 1259 OID 16787)
-- Name: sales_reports; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.sales_reports (
    id integer NOT NULL,
    car_id integer,
    customer_id character varying(50),
    dealer_id integer,
    sale_date timestamp without time zone NOT NULL,
    sale_price numeric(10,2) NOT NULL
);


ALTER TABLE public.sales_reports OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 16786)
-- Name: sales_reports_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.sales_reports_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sales_reports_id_seq OWNER TO postgres;

--
-- TOC entry 3711 (class 0 OID 0)
-- Dependencies: 229
-- Name: sales_reports_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.sales_reports_id_seq OWNED BY public.sales_reports.id;


--
-- TOC entry 226 (class 1259 OID 16739)
-- Name: test_drive_requests; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.test_drive_requests (
    id integer NOT NULL,
    car_id integer,
    customer_id character varying(50),
    dealer_id integer,
    request_date timestamp without time zone NOT NULL,
    status character varying(20) DEFAULT 'Bekliyor'::character varying,
    CONSTRAINT test_drive_requests_status_check CHECK (((status)::text = ANY ((ARRAY['Bekliyor'::character varying, 'Reddedildi'::character varying, 'Onaylandı'::character varying])::text[])))
);


ALTER TABLE public.test_drive_requests OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16738)
-- Name: test_drive_requests_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.test_drive_requests_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.test_drive_requests_id_seq OWNER TO postgres;

--
-- TOC entry 3712 (class 0 OID 0)
-- Dependencies: 225
-- Name: test_drive_requests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.test_drive_requests_id_seq OWNED BY public.test_drive_requests.id;


--
-- TOC entry 232 (class 1259 OID 16809)
-- Name: test_drives; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.test_drives (
    id integer NOT NULL,
    car_id integer,
    customer_id character varying(50),
    dealer_id integer,
    start_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    end_date timestamp without time zone,
    status character varying(20) DEFAULT 'Devam Ediyor'::character varying,
    notes text
);


ALTER TABLE public.test_drives OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 16808)
-- Name: test_drives_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.test_drives_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.test_drives_id_seq OWNER TO postgres;

--
-- TOC entry 3713 (class 0 OID 0)
-- Dependencies: 231
-- Name: test_drives_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.test_drives_id_seq OWNED BY public.test_drives.id;


--
-- TOC entry 3489 (class 2604 OID 16704)
-- Name: cars id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cars ALTER COLUMN id SET DEFAULT nextval('public.cars_id_seq'::regclass);


--
-- TOC entry 3488 (class 2604 OID 16675)
-- Name: dealers id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dealers ALTER COLUMN id SET DEFAULT nextval('public.dealers_id_seq'::regclass);


--
-- TOC entry 3495 (class 2604 OID 16766)
-- Name: orders id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders ALTER COLUMN id SET DEFAULT nextval('public.orders_id_seq'::regclass);


--
-- TOC entry 3491 (class 2604 OID 16718)
-- Name: price_offers id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.price_offers ALTER COLUMN id SET DEFAULT nextval('public.price_offers_id_seq'::regclass);


--
-- TOC entry 3497 (class 2604 OID 16790)
-- Name: sales_reports id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sales_reports ALTER COLUMN id SET DEFAULT nextval('public.sales_reports_id_seq'::regclass);


--
-- TOC entry 3493 (class 2604 OID 16742)
-- Name: test_drive_requests id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.test_drive_requests ALTER COLUMN id SET DEFAULT nextval('public.test_drive_requests_id_seq'::regclass);


--
-- TOC entry 3498 (class 2604 OID 16812)
-- Name: test_drives id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.test_drives ALTER COLUMN id SET DEFAULT nextval('public.test_drives_id_seq'::regclass);


--
-- TOC entry 3689 (class 0 OID 16685)
-- Dependencies: 220
-- Data for Name: admins; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.admins (username, password, dealer_id, name, age, phone) FROM stdin;
admin1	adminpass1	1	Ali Yıldız	40	5556667788
admin2	adminpass2	2	Veli Korkmaz	38	5557778899
admin3	adminpass3	3	Zeynep Şahin	35	5558889900
\.


--
-- TOC entry 3691 (class 0 OID 16701)
-- Dependencies: 222
-- Data for Name: cars; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.cars (id, brand, model, year, package_type, price, dealer, status) FROM stdin;
4	Ford	Focus	2022	Titanium	410000.00	2	Test Sürüşünde
5	Volkswagen	Golf	2023	Highline	550000.00	3	Stokta
6	BMW	3 Serisi	2022	Luxury	850000.00	3	Satıldı
7	Renault	Symbol	2015	Lüx	500000.00	1	Satıldı
18	BMW	M7	2022	Premium	1500000.00	1	Satıldı
17	Fiat	Linea	2020	Lüx	500000.00	1	Satıldı
19	Seat	Leon	2019	Deluxe	700000.00	1	Satıldı
11	Hyundai	i20	2018	Premium	680000.00	1	Test Sürüşünde
21	Tesla	Model S	2022	Premium	2000000.00	1	Stokta
8	BMW	M5	2020	Premium	1200000.00	1	Satıldı
22	Tesla	Model E	2022	Deluxe	2000000.00	1	Stokta
10	Mercedes	c180	2023	Deluxe	1800000.00	1	Satıldı
2	Honda	Civic	2021	Executive	520000.00	1	Satıldı
9	Honda	Jazz	2024	Regular	800000.00	1	Satıldı
3	Renault	Megane	2023	Business	380000.00	2	Satıldı
12	Renault	Clio	2020	Regular	800000.00	1	Satıldı
13	Renault	Megane	2014	Deluxe	500000.00	1	Satıldı
14	Ford	Mustang	1969	Premium	10000000.00	1	Satıldı
15	Fiat	Egea	2020	Standat	700000.00	1	Satıldı
1	Toyota	Corolla	2022	Comfort	450000.00	1	Satıldı
16	Ford	Focus	2020	Regular	900000.00	1	Satıldı
20	Fiat	Doblo	2018	Regular	700000.00	1	Stokta
\.


--
-- TOC entry 3688 (class 0 OID 16680)
-- Dependencies: 219
-- Data for Name: customers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.customers (username, password, name, age, phone, registration_date) FROM stdin;
ahmet.yilmaz	sifre123	Ahmet Yılmaz	35	5551112233	2015-06-14 08:45:00
mehmet.kaya	sifre456	Mehmet Kaya	28	5552223344	2016-11-23 14:20:00
ayse.demir	sifre789	Ayşe Demir	42	5553334455	2017-03-05 09:10:00
fatma.celik	sifre012	Fatma Çelik	31	5554445566	2018-09-17 18:30:00
mustafa.arslan	sifre345	Mustafa Arslan	45	5555556677	2020-01-29 11:55:00
gokay.gunbak	123	Gökay Günbak	22	5070403863	2025-05-13 12:12:00
tolga.ozturk	123	Tolga Öztürk	23	5556667788	2025-05-20 05:03:00
\.


--
-- TOC entry 3687 (class 0 OID 16672)
-- Dependencies: 218
-- Data for Name: dealers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dealers (id, name, address, phone, email, admin_id) FROM stdin;
1	İstanbul Oto	Maslak, İstanbul	2121112233	info@istanbuloto.com	admin1
2	Ankara Otomotiv	Çankaya, Ankara	3122223344	satis@ankaraotomotiv.com	admin2
3	İzmir Araba	Karşıyaka, İzmir	2323334455	iletisim@izmiraraba.com	admin3
\.


--
-- TOC entry 3697 (class 0 OID 16763)
-- Dependencies: 228
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.orders (id, car_id, customer_id, dealer_id, order_date, status) FROM stdin;
1	6	ayse.demir	3	2023-05-10 11:20:00	Onaylandı
2	3	mehmet.kaya	2	2023-05-12 09:45:00	Bekliyor
\.


--
-- TOC entry 3693 (class 0 OID 16715)
-- Dependencies: 224
-- Data for Name: price_offers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.price_offers (id, car_id, customer_id, dealer_id, offer_price, offer_date, status) FROM stdin;
2	3	mehmet.kaya	2	370000.00	2023-05-15 14:45:00	Onaylandı
3	5	ayse.demir	3	530000.00	2023-05-16 11:20:00	Reddedildi
4	7	ahmet.yilmaz	1	480000.00	2025-05-12 12:13:00	Onaylandı
5	5	ahmet.yilmaz	3	500000.00	2025-05-13 12:00:00	Bekliyor
6	3	ahmet.yilmaz	2	250000.00	2025-05-20 12:12:00	Onaylandı
1	1	ahmet.yilmaz	1	430000.00	2023-05-14 09:30:00	Onaylandı
\.


--
-- TOC entry 3699 (class 0 OID 16787)
-- Dependencies: 230
-- Data for Name: sales_reports; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.sales_reports (id, car_id, customer_id, dealer_id, sale_date, sale_price) FROM stdin;
1	6	ayse.demir	3	2023-05-11 16:30:00	850000.00
5	7	ahmet.yilmaz	1	2025-05-12 23:01:10.214378	480000.00
10	8	mustafa.arslan	1	2025-05-13 10:10:00	1200000.00
11	10	mustafa.arslan	1	2025-06-28 12:12:00	1800000.00
12	2	fatma.celik	1	2025-05-19 22:12:00	520000.00
13	9	mehmet.kaya	1	2025-06-13 11:11:00	800000.00
14	3	ahmet.yilmaz	2	2025-05-13 20:49:00.108156	250000.00
15	12	mehmet.kaya	1	2025-06-13 09:09:00	800000.00
16	13	ahmet.yilmaz	1	2025-07-13 14:14:00	500000.00
17	14	mustafa.arslan	1	2024-05-13 08:08:00	10000000.00
18	15	fatma.celik	1	2023-04-10 10:40:00	700000.00
19	1	ahmet.yilmaz	1	2025-05-20 12:31:37.535156	430000.00
20	16	tolga.ozturk	1	2025-07-20 12:12:00	900000.00
21	18	tolga.ozturk	1	2024-12-20 12:12:00	1500000.00
22	17	gokay.gunbak	1	2022-01-20 05:05:00	500000.00
23	19	tolga.ozturk	1	2025-04-20 03:03:00	700000.00
\.


--
-- TOC entry 3695 (class 0 OID 16739)
-- Dependencies: 226
-- Data for Name: test_drive_requests; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.test_drive_requests (id, car_id, customer_id, dealer_id, request_date, status) FROM stdin;
1	2	fatma.celik	1	2023-05-15 10:00:00	Onaylandı
2	4	mustafa.arslan	2	2023-05-16 14:30:00	Bekliyor
4	3	ahmet.yilmaz	2	2025-05-13 12:12:00	Bekliyor
3	1	ahmet.yilmaz	1	2025-05-13 09:00:00	Onaylandı
5	21	tolga.ozturk	1	2025-05-20 09:00:00	Onaylandı
\.


--
-- TOC entry 3701 (class 0 OID 16809)
-- Dependencies: 232
-- Data for Name: test_drives; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.test_drives (id, car_id, customer_id, dealer_id, start_date, end_date, status, notes) FROM stdin;
1	18	tolga.ozturk	1	2025-05-20 13:46:50.379463	\N	Devam Ediyor	\N
2	18	tolga.ozturk	1	2025-05-20 13:49:52.978372	\N	Devam Ediyor	\N
3	20	tolga.ozturk	1	2025-05-20 13:54:42.259693	\N	Devam Ediyor	\N
4	11	gokay.gunbak	1	2025-05-20 14:51:22.584458	\N	Devam Ediyor	\N
\.


--
-- TOC entry 3714 (class 0 OID 0)
-- Dependencies: 221
-- Name: cars_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.cars_id_seq', 22, true);


--
-- TOC entry 3715 (class 0 OID 0)
-- Dependencies: 217
-- Name: dealers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.dealers_id_seq', 3, true);


--
-- TOC entry 3716 (class 0 OID 0)
-- Dependencies: 227
-- Name: orders_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.orders_id_seq', 2, true);


--
-- TOC entry 3717 (class 0 OID 0)
-- Dependencies: 223
-- Name: price_offers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.price_offers_id_seq', 6, true);


--
-- TOC entry 3718 (class 0 OID 0)
-- Dependencies: 229
-- Name: sales_reports_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.sales_reports_id_seq', 23, true);


--
-- TOC entry 3719 (class 0 OID 0)
-- Dependencies: 225
-- Name: test_drive_requests_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.test_drive_requests_id_seq', 5, true);


--
-- TOC entry 3720 (class 0 OID 0)
-- Dependencies: 231
-- Name: test_drives_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.test_drives_id_seq', 4, true);


--
-- TOC entry 3510 (class 2606 OID 16689)
-- Name: admins admins_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admins
    ADD CONSTRAINT admins_pkey PRIMARY KEY (username);


--
-- TOC entry 3512 (class 2606 OID 16708)
-- Name: cars cars_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cars
    ADD CONSTRAINT cars_pkey PRIMARY KEY (id);


--
-- TOC entry 3508 (class 2606 OID 16684)
-- Name: customers customers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customers
    ADD CONSTRAINT customers_pkey PRIMARY KEY (username);


--
-- TOC entry 3506 (class 2606 OID 16679)
-- Name: dealers dealers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dealers
    ADD CONSTRAINT dealers_pkey PRIMARY KEY (id);


--
-- TOC entry 3518 (class 2606 OID 16770)
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- TOC entry 3514 (class 2606 OID 16722)
-- Name: price_offers price_offers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.price_offers
    ADD CONSTRAINT price_offers_pkey PRIMARY KEY (id);


--
-- TOC entry 3520 (class 2606 OID 16792)
-- Name: sales_reports sales_reports_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sales_reports
    ADD CONSTRAINT sales_reports_pkey PRIMARY KEY (id);


--
-- TOC entry 3516 (class 2606 OID 16746)
-- Name: test_drive_requests test_drive_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.test_drive_requests
    ADD CONSTRAINT test_drive_requests_pkey PRIMARY KEY (id);


--
-- TOC entry 3522 (class 2606 OID 16818)
-- Name: test_drives test_drives_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.test_drives
    ADD CONSTRAINT test_drives_pkey PRIMARY KEY (id);


--
-- TOC entry 3524 (class 2606 OID 16690)
-- Name: admins admins_dealer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.admins
    ADD CONSTRAINT admins_dealer_id_fkey FOREIGN KEY (dealer_id) REFERENCES public.dealers(id);


--
-- TOC entry 3525 (class 2606 OID 16709)
-- Name: cars cars_dealer_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cars
    ADD CONSTRAINT cars_dealer_fkey FOREIGN KEY (dealer) REFERENCES public.dealers(id);


--
-- TOC entry 3523 (class 2606 OID 16695)
-- Name: dealers dealers_admin_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dealers
    ADD CONSTRAINT dealers_admin_id_fkey FOREIGN KEY (admin_id) REFERENCES public.admins(username);


--
-- TOC entry 3532 (class 2606 OID 16771)
-- Name: orders orders_car_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_car_id_fkey FOREIGN KEY (car_id) REFERENCES public.cars(id);


--
-- TOC entry 3533 (class 2606 OID 16776)
-- Name: orders orders_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES public.customers(username);


--
-- TOC entry 3534 (class 2606 OID 16781)
-- Name: orders orders_dealer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_dealer_id_fkey FOREIGN KEY (dealer_id) REFERENCES public.dealers(id);


--
-- TOC entry 3526 (class 2606 OID 16723)
-- Name: price_offers price_offers_car_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.price_offers
    ADD CONSTRAINT price_offers_car_id_fkey FOREIGN KEY (car_id) REFERENCES public.cars(id);


--
-- TOC entry 3527 (class 2606 OID 16728)
-- Name: price_offers price_offers_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.price_offers
    ADD CONSTRAINT price_offers_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES public.customers(username);


--
-- TOC entry 3528 (class 2606 OID 16733)
-- Name: price_offers price_offers_dealer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.price_offers
    ADD CONSTRAINT price_offers_dealer_id_fkey FOREIGN KEY (dealer_id) REFERENCES public.dealers(id);


--
-- TOC entry 3535 (class 2606 OID 16793)
-- Name: sales_reports sales_reports_car_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sales_reports
    ADD CONSTRAINT sales_reports_car_id_fkey FOREIGN KEY (car_id) REFERENCES public.cars(id);


--
-- TOC entry 3536 (class 2606 OID 16798)
-- Name: sales_reports sales_reports_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sales_reports
    ADD CONSTRAINT sales_reports_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES public.customers(username);


--
-- TOC entry 3537 (class 2606 OID 16803)
-- Name: sales_reports sales_reports_dealer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.sales_reports
    ADD CONSTRAINT sales_reports_dealer_id_fkey FOREIGN KEY (dealer_id) REFERENCES public.dealers(id);


--
-- TOC entry 3529 (class 2606 OID 16747)
-- Name: test_drive_requests test_drive_requests_car_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.test_drive_requests
    ADD CONSTRAINT test_drive_requests_car_id_fkey FOREIGN KEY (car_id) REFERENCES public.cars(id);


--
-- TOC entry 3530 (class 2606 OID 16752)
-- Name: test_drive_requests test_drive_requests_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.test_drive_requests
    ADD CONSTRAINT test_drive_requests_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES public.customers(username);


--
-- TOC entry 3531 (class 2606 OID 16757)
-- Name: test_drive_requests test_drive_requests_dealer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.test_drive_requests
    ADD CONSTRAINT test_drive_requests_dealer_id_fkey FOREIGN KEY (dealer_id) REFERENCES public.dealers(id);


--
-- TOC entry 3538 (class 2606 OID 16819)
-- Name: test_drives test_drives_car_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.test_drives
    ADD CONSTRAINT test_drives_car_id_fkey FOREIGN KEY (car_id) REFERENCES public.cars(id);


--
-- TOC entry 3539 (class 2606 OID 16824)
-- Name: test_drives test_drives_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.test_drives
    ADD CONSTRAINT test_drives_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES public.customers(username);


--
-- TOC entry 3540 (class 2606 OID 16829)
-- Name: test_drives test_drives_dealer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.test_drives
    ADD CONSTRAINT test_drives_dealer_id_fkey FOREIGN KEY (dealer_id) REFERENCES public.dealers(id);


-- Completed on 2025-05-21 14:03:34 +03

--
-- PostgreSQL database dump complete
--

