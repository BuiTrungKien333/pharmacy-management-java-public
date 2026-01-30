-- Hello world
-------------------------------------------------------------------------------------------
-- 1. Create database: AlamiPharmacyDB
-------------------------------------------------------------------------------------------
USE master;
GO

-- ALTER DATABASE AlamiPharmacyDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
-- DROP DATABASE AlamiPharmacyDB;

CREATE DATABASE AlamiPharmacyDB_v4;
GO

USE AlamiPharmacyDB_v4;
GO

-------------------------------------------------------------------------------------------
-- 2. Create Sequence + Table
-------------------------------------------------------------------------------------------
-- Sequence
CREATE SEQUENCE Emp_Seq START WITH 1 INCREMENT BY 1;
go

CREATE SEQUENCE Vou_Seq START WITH 1 INCREMENT BY 1;
go

CREATE SEQUENCE Invoice_Seq START WITH 1 INCREMENT BY 1;
go

CREATE SEQUENCE Refund_Seq START WITH 1 INCREMENT BY 1;
go

CREATE SEQUENCE LoThuoc_Seq START WITH 1 INCREMENT BY 1;
go

-------------------------------------------------------------------------------------------
-- Table 
-------------------------------------------------------------------------------------------
-- Role
CREATE TABLE [tbl_role] (
  [id] INT PRIMARY KEY IDENTITY(1,1),
  role_name VARCHAR(30)
);
GO

-- Permission
create table tbl_permission (
   id int primary key identity(1,1),
   permission_key varchar(100)
);
go

-- Role with permission 
create table tbl_role_permission (
  role_id int not null,
  permission_id int not null,
  CONSTRAINT pk_role_permission PRIMARY KEY (role_id, permission_id),
  CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES tbl_role(id),
  CONSTRAINT fk_permission FOREIGN KEY (permission_id) REFERENCES tbl_permission(id)
);
go

-- Cửa hàng / Kho
CREATE TABLE tbl_cua_hang (
  id TINYINT PRIMARY KEY IDENTITY(1,1),
  ten_cua_hang nvarchar(100),
  dia_chi nvarchar(200),
  so_dang_ky varchar(50),
  giay_chung_nhan varchar(100)
);
GO

-- Trạng thái lô thuốc
CREATE TABLE [tbl_trang_thai_lo] (
  [id] TINYINT PRIMARY KEY IDENTITY(1,1),
  [ten_trang_thai] NVARCHAR(20)
);
GO

-- Nhà cung cấp
CREATE TABLE tbl_nha_cung_cap (
  id TINYINT PRIMARY KEY IDENTITY(1,1),
  ma_nha_may varchar(10) unique,
  ten_nha_cung_cap nvarchar(200),
  dia_chi nvarchar(200),
  so_dien_thoai varchar(20),
  ma_so_thue VARCHAR(50),
  email varchar(40),
  website varchar(50),
  ghi_chu nvarchar(200)
);
GO

CREATE TABLE tbl_dang_bao_che (
  id TINYINT IDENTITY(1,1) PRIMARY KEY,
  ten_dang_bao_che NVARCHAR(50)
);
go

CREATE TABLE tbl_duong_dung (
  id TINYINT IDENTITY(1,1) PRIMARY KEY,
  ten_duong_dung NVARCHAR(50)
);
go

CREATE TABLE tbl_tieu_chuan_chat_luong (
  id TINYINT IDENTITY(1,1) PRIMARY KEY,
  ten_tieu_chuan NVARCHAR(50)
);
go

CREATE TABLE tbl_don_vi_tinh (
  id TINYINT IDENTITY(1,1) PRIMARY KEY,
  ten_dvt NVARCHAR(50)
);
go

-- Loại sản phẩm
CREATE TABLE tbl_loai_san_pham (
  id TINYINT PRIMARY KEY IDENTITY(1,1),
  ten_loai nvarchar(50)
);
GO

-- Nhân viên 
create table tbl_nhan_vien (
	ma_nv varchar(10) primary key default ('ALA01' + right('0000' + cast(next value for Emp_Seq as varchar(4)), 4)),
	ho_ten nvarchar(100),
	so_dien_thoai varchar(11) unique,
	email varchar(50) unique,
	ngay_sinh date,
	gioi_tinh bit,
	dia_chi nvarchar(255),
	[cua_hang_id] tinyint,
	avatar_url varchar(100),
	ngay_vao_lam date,

	deleted bit default 0,
	created_at smalldatetime default GETDATE(),
	update_at smalldatetime,
	deleted_at smalldatetime,

    CONSTRAINT [fk_nhan_vien_cua_hang] FOREIGN KEY ([cua_hang_id]) REFERENCES [tbl_cua_hang]([id])
);
go

-- user with role
create table tbl_user_role (
  user_id varchar(10) not null,
  role_id int not null,
  CONSTRAINT pk_user_has_role PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES tbl_role(id),
  CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES tbl_nhan_vien(ma_nv)
);
go

-- Tài khoản (user_name = ma_nv)
create table tbl_tai_khoan (
	user_name varchar(10) primary key,
	password varchar(255) not null,
	otp_key varchar(7) null,
	expiry_time datetime2 null, -- lưu thời gian hết hạn của mã OTP
	account_locked bit default 0,

	created_at smalldatetime default GETDATE(),
	update_at smalldatetime,
	deleted_at smalldatetime null,

	constraint FK_TK_NV foreign key (user_name) references tbl_nhan_vien(ma_nv)
);
go

-- Hạng thành viên (normal: 1000, silver: 3000, gold: 5000)
-- 0 < diem_tich_luy < 1000 -> normal 
-- 1000 <= diem_tich_luy < 3000 -> silver 
-- diem_tich_luy >= 3000 -> gold
CREATE TABLE tbl_hang_thanh_vien ( 
  id TINYINT PRIMARY KEY IDENTITY(1,1),
  hang_thanh_vien varchar(20),
  mo_ta nvarchar(100),
  diem_toi_thieu int
);
GO

-- Khách hàng 
CREATE TABLE tbl_khach_hang (
  id int PRIMARY KEY IDENTITY(1,1),
  ho_ten nvarchar(100),
  so_dien_thoai varchar(11) UNIQUE,
  hang_thanh_vien tinyint,
  diem_tich_luy int DEFAULT 0,
  created_at smalldatetime DEFAULT GETDATE(),

  CONSTRAINT [fk_kh_htv] FOREIGN KEY ([hang_thanh_vien]) REFERENCES [tbl_hang_thanh_vien]([id])
);
GO

--  Voucher -> VCHDDMMYYXXXX -> VCH: viết tắt của voucher, DD: day, MM: month, YY: year, XXXX: increment
CREATE TABLE tbl_voucher (
  ma_voucher varchar(13) primary key
  default (CONCAT( 'VCH', 
  RIGHT('00' + CAST(DAY(GETDATE()) AS VARCHAR(2)), 2),
  RIGHT('00' + CAST(MONTH(GETDATE()) AS VARCHAR(2)), 2),
  RIGHT(CAST(YEAR(GETDATE()) AS VARCHAR(4)), 2), 
  RIGHT('0000' + CAST(NEXT VALUE FOR Vou_Seq AS VARCHAR(4)), 4))),

  -- Giá trị: giảm theo %, ví dụ: 5% thì nhập là 5 
  gia_tri int,

  don_toi_thieu decimal(18,2) DEFAULT 0,   -- đơn tối thiểu để áp dụng
  giam_toi_da decimal(18,2) DEFAULT 0,     -- số tiền giảm tối đa (áp dụng khi giảm %)
  ngay_bat_dau date,
  ngay_ket_thuc date,
  so_lan_da_dung int DEFAULT 0,
  [so_luot_su_dung_toi_da] INT,
  dieu_kien_hang_tv tinyint default 1,

  deleted bit default 0,
  created_at smalldatetime default GETDATE(),
  update_at smalldatetime,
  deleted_at smalldatetime,

  CONSTRAINT [fk_voucher_hang_tv] FOREIGN KEY ([dieu_kien_hang_tv]) REFERENCES [tbl_hang_thanh_vien]([id])
);
GO

-- Sản phẩm
CREATE TABLE tbl_san_pham (
  id INT IDENTITY(1,1) PRIMARY KEY,

  barcode varchar(15) unique,                      -- 8809192001929
  ten_sp NVARCHAR(100),                            -- Tên sản phẩm, ví dụ: Panadol Extra
  ten_sp_khong_dau VARCHAR(100),                   -- panadol extra
  hoat_chat_ham_luong NVARCHAR(255),               -- Paracetamol 500mg, Caffeine 65mg
  dang_bao_che NVARCHAR(50),                       -- Viên nén bao phim
  duong_dung NVARCHAR(50),                         -- Đường dùng (uống, tiêm,...)

  chi_dinh NVARCHAR(255),                          -- Chỉ định sử dụng
  chong_chi_dinh NVARCHAR(255),                    -- Chống chỉ định

  lieu_dung NVARCHAR(255), 
  so_dang_ky NVARCHAR(20) UNIQUE,                  -- VD-29584-18
  nuoc_san_xuat NVARCHAR(100),                     -- Nước sản xuất
  nha_san_xuat NVARCHAR(255),                      -- Tên nhà sản xuất
  tieu_chuan_chat_luong NVARCHAR(50),              -- TCCS, Dược điển,...

  quy_cach_dong_goi NVARCHAR(100),                 -- Ví dụ: Hộp 10 vỉ x 12 viên
  don_vi_tinh nvarchar(50),                        -- Viên, Vỉ, Hộp, Lọ, Chai, Ống, Gói, Tuýp, Hộp nhỏ, Vỉ nhỏ
  mo_ta NVARCHAR(MAX),						       -- Mô tả ngắn 

  avatar_url VARCHAR(50),                          -- Đường dẫn ảnh (barcode.png)
  tong_so_luong INT DEFAULT 0,                     -- Tổng số lượng tồn

  loai_sp_id TINYINT,                              -- FK -> loại sản phẩm (1, 2, 3)
  deleted BIT DEFAULT 0,                           -- 1: đã xóa mềm

  created_at SMALLDATETIME DEFAULT GETDATE(),
  updated_at SMALLDATETIME,
  deleted_at SMALLDATETIME,

  CONSTRAINT fk_san_pham_loai FOREIGN KEY (loai_sp_id) REFERENCES tbl_loai_san_pham(id)
);
GO

-- Lô thuốc
-- LSX-DDMMYY-AA-NNNNNN-XXX -> LSX: viết tắt của "Lô sản xuất", DDMMYY: dd/MM/yy , AA: mã nhà máy, NNNNNN: mã sản phẩm, XXX: tự động tăng
CREATE TABLE tbl_lo_thuoc (
  so_lo VARCHAR(30) PRIMARY KEY,                   -- Số lô sản xuất
  ngay_san_xuat DATE,                              -- Ngày sản xuất
  han_su_dung DATE,                                -- Hạn sử dụng
  ngay_nhap DATETIME DEFAULT GETDATE(),            -- Ngày nhập kho
  so_luong_nhap INT NOT NULL,                      -- Số lượng nhập theo đơn vị gốc
  so_luong_con INT,                                -- Số lượng còn lại theo đơn vị gốc
  gia_nhap DECIMAL(18,2),                          -- Giá nhập 1 đơn vị gốc (VD: 1 hộp)
  thanh_tien AS (gia_nhap * so_luong_nhap) PERSISTED, -- Thành tiền tự tính
  gia_ban DECIMAL(18,2),

  ma_sp int NOT NULL,                              -- FK -> tbl_san_pham
  ma_ncc TINYINT NOT NULL,                         -- FK -> nhà cung cấp
  ma_nv VARCHAR(10) NOT NULL,                      -- FK -> nhân viên nhập
  trang_thai_id TINYINT DEFAULT 1,                 -- FK -> trạng thái lô (1: Còn hàng, 2: Hết, 3: Hủy,...)

  created_at SMALLDATETIME DEFAULT GETDATE(),
  updated_at SMALLDATETIME,

  CONSTRAINT fk_lothuoc_sp FOREIGN KEY (ma_sp) REFERENCES tbl_san_pham(id),
  CONSTRAINT fk_lothuoc_trangthai FOREIGN KEY (trang_thai_id) REFERENCES tbl_trang_thai_lo(id),
  CONSTRAINT fk_lothuoc_ncc FOREIGN KEY (ma_ncc) REFERENCES tbl_nha_cung_cap(id),
  CONSTRAINT fk_lothuoc_nv FOREIGN KEY (ma_nv) REFERENCES tbl_nhan_vien(ma_nv)
);
GO

-- Hóa đơn bán -> HDDDMMYYXXXX - HD2809250001
CREATE TABLE tbl_hoa_don (
  id varchar(12) primary key
  default (CONCAT('HD', 
  RIGHT('00' + CAST(DAY(GETDATE()) AS VARCHAR(2)), 2),
  RIGHT('00' + CAST(MONTH(GETDATE()) AS VARCHAR(2)), 2),
  RIGHT(CAST(YEAR(GETDATE()) AS VARCHAR(4)), 2), 
  RIGHT('0000' + CAST(NEXT VALUE FOR Invoice_Seq AS VARCHAR(4)), 4))),

  ma_kh int null,
  ma_nv varchar(10),
  ma_vou varchar(13) null,
  ngay_lap datetime DEFAULT GETDATE(),
  tong_tien decimal(18,2),
  tong_tien_sau_giam decimal(18,2),
  da_tra bit default 0
);
GO
ALTER TABLE tbl_hoa_don ADD CONSTRAINT fk_hd_kh FOREIGN KEY (ma_kh) REFERENCES tbl_khach_hang(id);
ALTER TABLE tbl_hoa_don ADD CONSTRAINT fk_hd_nv FOREIGN KEY (ma_nv) REFERENCES tbl_nhan_vien(ma_nv);
ALTER TABLE tbl_hoa_don ADD CONSTRAINT fk_hd_voucher FOREIGN KEY (ma_vou) REFERENCES tbl_voucher(ma_voucher);
go

-- Chi tiết hóa đơn
CREATE TABLE tbl_chi_tiet_hoa_don (
  id INT IDENTITY(1,1) PRIMARY KEY,

  ma_hd VARCHAR(12) NOT NULL,              -- Mã hóa đơn (FK -> tbl_hoa_don)
  ma_sp int NOT NULL,               -- Mã sản phẩm (FK -> tbl_san_pham)
  so_lo varchar(30),

  so_luong INT, -- Số lượng bán
  don_gia DECIMAL(18,2), -- Giá bán
  gia_goc decimal(18,2),
  thanh_tien AS (so_luong * don_gia) PERSISTED,        -- Thành tiền tự tính
  ghi_chu NVARCHAR(100),

  created_at SMALLDATETIME DEFAULT GETDATE(),

  CONSTRAINT fk_cthd_hd FOREIGN KEY (ma_hd) REFERENCES tbl_hoa_don(id),
  CONSTRAINT fk_cthd_sp FOREIGN KEY (ma_sp) REFERENCES tbl_san_pham(id),
  CONSTRAINT fk_cthd_solo FOREIGN KEY (so_lo) REFERENCES tbl_lo_thuoc(so_lo)
);
GO

-- Hóa đơn trả -> HDTDDMMYYXXXX - HDT2809250001
CREATE TABLE tbl_hoa_don_tra (
  id varchar(13) primary key
  default (CONCAT('HDT', 
  RIGHT('00' + CAST(DAY(GETDATE()) AS VARCHAR(2)), 2),
  RIGHT('00' + CAST(MONTH(GETDATE()) AS VARCHAR(2)), 2),
  RIGHT(CAST(YEAR(GETDATE()) AS VARCHAR(4)), 2), 
  RIGHT('0000' + CAST(NEXT VALUE FOR Refund_Seq AS VARCHAR(4)), 4))),

  ma_kh int,
  ma_nv varchar(10),
  ma_hd varchar(12),
  ngay_lap datetime DEFAULT (GETDATE()),
  tien_hoan decimal(18,2),
  ly_do nvarchar(255),
  da_duyet bit default 0
);
GO

ALTER TABLE tbl_hoa_don_tra ADD CONSTRAINT fk_hdtra_kh FOREIGN KEY (ma_kh) REFERENCES tbl_khach_hang(id);
ALTER TABLE tbl_hoa_don_tra ADD CONSTRAINT fk_hdtra_nv FOREIGN KEY (ma_nv) REFERENCES tbl_nhan_vien(ma_nv);
ALTER TABLE tbl_hoa_don_tra ADD CONSTRAINT fk_hdtra_hd FOREIGN KEY (ma_hd) REFERENCES tbl_hoa_don(id);
go

-- Chi tiết hóa đơn trả
CREATE TABLE tbl_chi_tiet_hoa_don_tra (
  id int PRIMARY KEY IDENTITY(1,1),
  so_luong int,
  don_gia decimal(18,2),
  thanh_tien AS (so_luong * don_gia) PERSISTED,
  so_lo varchar(30),
  ma_sp int,
  ma_hd_tra varchar(13),
  trang_thai_id bit default 0,
  huong_xu_ly nvarchar(50),
  ly_do nvarchar(100)
);
GO

ALTER TABLE tbl_chi_tiet_hoa_don_tra ADD CONSTRAINT fk_cthdtra_lo FOREIGN KEY (so_lo) REFERENCES tbl_lo_thuoc(so_lo);
ALTER TABLE tbl_chi_tiet_hoa_don_tra ADD CONSTRAINT fk_cthdtra_hdtra FOREIGN KEY (ma_hd_tra) REFERENCES tbl_hoa_don_tra(id);
ALTER TABLE tbl_chi_tiet_hoa_don_tra ADD CONSTRAINT fk_cthdtra_sp FOREIGN KEY (ma_sp) REFERENCES tbl_san_pham(id);
GO

-------------------------------------------------------------------------------------------
-- 2. Trigger
-------------------------------------------------------------------------------------------
-- Khi thêm mới một nhân viên thì tự động thêm mới một tài khoản, user_name là mã nhân viên và mật khẩu ban đầu là 123
create trigger trg_auto_create_account 
on tbl_nhan_vien
after insert
as
begin
	set nocount on;

	insert into tbl_tai_khoan(user_name, password)
	select i.ma_nv, '$2a$06$BUEUzj427SXa0pHNKRTNeenNBgE20PR6ChI0GOpOY5zTWgtAKUMaG'
	from inserted i;
end;
go

-- Sau khi cập nhật tài khoản 
create trigger trg_upd_account
on tbl_tai_khoan
after update 
as 
begin
	SET NOCOUNT ON;
	update t set t.update_at = GETDATE()
	from tbl_tai_khoan t
	inner join inserted i on t.user_name = i.user_name
end;
go

-- Sau khi cập nhật thông tin nhân viên
create trigger trg_upd_emp
on tbl_nhan_vien
after update 
as 
begin
	SET NOCOUNT ON;
	update t set t.update_at = GETDATE()
	from tbl_nhan_vien t
	inner join inserted i on t.ma_nv = i.ma_nv
end;
go

-- Xoá một nhân viên thì khóa luôn tài khoản của nhân viên đó. (user_name = ma_nv)
create trigger trg_soft_del_emp
on tbl_nhan_vien
instead of delete
as
begin
	set nocount on;

	update tk set tk.account_locked=1, tk.deleted_at=GETDATE()
	from tbl_tai_khoan tk inner join deleted d on tk.user_name = d.ma_nv;

	update nv set nv.deleted=1, nv.deleted_at=GETDATE()
	from tbl_nhan_vien nv inner join deleted d on nv.ma_nv = d.ma_nv;
end;
go

-- Trigger tự động cập nhật hạng thành viên khi tăng điểm tích luỹ hoặc thêm mới khách hàng
create trigger trg_update_rank_cus
on tbl_khach_hang
after update
as
begin
	
	set nocount on;

	update kh
	set hang_thanh_vien = (select top 1 id from tbl_hang_thanh_vien where i.diem_tich_luy >= diem_toi_thieu order by diem_toi_thieu desc)
	from tbl_khach_hang kh 
	inner join inserted i on kh.id = i.id
	inner join deleted d on d.id = i.id
	where i.diem_tich_luy <> d.diem_tich_luy

end;
go

create trigger trg_update_rank_cus_after_insert
on tbl_khach_hang
after insert
as
begin
	
	set nocount on;

	update kh
	set hang_thanh_vien = (select top 1 id from tbl_hang_thanh_vien where i.diem_tich_luy >= diem_toi_thieu order by diem_toi_thieu desc)
	from tbl_khach_hang kh inner join inserted i on i.id = kh.id
end;
go

-- Tư động cập nhật tổng số lượng trong kho
CREATE TRIGGER trg_UpdateTongSoLuong
ON tbl_lo_thuoc
AFTER INSERT
AS
BEGIN
    UPDATE sp
    SET sp.tong_so_luong = sp.tong_so_luong + i.so_luong_nhap
    FROM tbl_san_pham sp
    INNER JOIN inserted i ON sp.id = i.ma_sp
END
GO

-- Trigger tự động cập nhật trạng thái lô sau khi bán hết
CREATE TRIGGER trg_auto_upd_status_lothuoc
ON tbl_lo_thuoc
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF TRIGGER_NESTLEVEL() > 1 RETURN;

    UPDATE lo
    SET
        lo.trang_thai_id = CASE WHEN i.so_luong_con = 0 THEN 3 ELSE 1 END,
        lo.updated_at = GETDATE()
    FROM tbl_lo_thuoc lo
    INNER JOIN inserted i ON lo.so_lo = i.so_lo
    WHERE
        i.trang_thai_id <> 2
        AND (i.so_luong_con <> (SELECT so_luong_con FROM deleted WHERE so_lo = i.so_lo)
             OR i.so_luong_con = 0);
END;
go

-- Trigger tự động cập nhật tổng số lượng trong kho khi chỉnh số lượng còn của lô
create trigger trg_auto_upd_qty_after_upd_slcon
on tbl_lo_thuoc
after update
as
begin 
    set nocount on;

    -- CASE 1: đang lưu hành -> đang lưu hành (chỉ cập nhật số lượng)
    update sp
    set sp.tong_so_luong = sp.tong_so_luong + (i.so_luong_con - d.so_luong_con)
    from inserted i
    inner join deleted d on i.so_lo = d.so_lo 
    inner join tbl_san_pham sp on sp.id = d.ma_sp
    where d.trang_thai_id = 1 
      and i.trang_thai_id = 1
      and d.so_luong_con != i.so_luong_con;

    -- CASE 2: đang lưu hành -> hủy lô
    update sp
    set sp.tong_so_luong = sp.tong_so_luong - d.so_luong_con
    from inserted i
    inner join deleted d on i.so_lo = d.so_lo
    inner join tbl_san_pham sp on sp.id = d.ma_sp
    where d.trang_thai_id = 1 
      and i.trang_thai_id = 2;

    -- CASE 3: hủy lô -> đang lưu hành
    update sp
    set sp.tong_so_luong = sp.tong_so_luong + i.so_luong_con
    from inserted i
    inner join deleted d on i.so_lo = d.so_lo 
    inner join tbl_san_pham sp on sp.id = d.ma_sp
    where d.trang_thai_id = 2 
      and i.trang_thai_id = 1;
end;
go


-------------------------------------------------------------------------------------------
-- 3. Store Procedure
-------------------------------------------------------------------------------------------
-- SP thêm mới lô thuốc
create procedure sp_insert_lo_thuoc
	@ngay_san_xuat date,
	@han_su_dung date,
	@so_luong_nhap int,
	@so_luong_con int,
	@gia_nhap decimal(18, 2),
	@gia_ban decimal(18, 2),
	@ma_sp int,
	@ma_ncc tinyint,
	@ma_nv varchar(10),
	@so_lo varchar(30) output

as
begin 
	set nocount on;

	SELECT @so_lo =
        'LSX-' 
        + RIGHT('00' + CAST(DAY(GETDATE()) AS VARCHAR(2)), 2) 
        + RIGHT('00' + CAST(MONTH(GETDATE()) AS VARCHAR(2)), 2)
        + RIGHT(CAST(YEAR(GETDATE()) % 100 AS VARCHAR(2)), 2) + '-' +
        ISNULL(ncc.ma_nha_may, 'AA') + '-' +
        FORMAT(@ma_sp, '000000') + '-' +
        RIGHT('000' + CAST(NEXT VALUE FOR LoThuoc_Seq AS VARCHAR(3)), 3)
    FROM tbl_nha_cung_cap ncc
    WHERE ncc.id = @ma_ncc;

    INSERT INTO tbl_lo_thuoc (
        so_lo, ngay_san_xuat, han_su_dung,
        so_luong_nhap, so_luong_con, gia_nhap, gia_ban,
        ma_sp, ma_ncc, ma_nv
    ) VALUES (
        @so_lo, @ngay_san_xuat, @han_su_dung,
        @so_luong_nhap, @so_luong_con, @gia_nhap, @gia_ban,
        @ma_sp, @ma_ncc, @ma_nv
    );

    SELECT @so_lo AS so_lo;

end;
go

-- SP tự động cập nhật lô thuốc hết hạn
CREATE OR ALTER PROCEDURE sp_KiemTraVaCapNhatHetHan
AS
BEGIN
    SET NOCOUNT ON; 

    BEGIN TRANSACTION;

    BEGIN TRY
        DECLARE @NgayHienTai DATE = CAST(GETDATE() AS DATE);

        -- BƯỚC 1: Trừ số lượng tổng ở bảng tbl_san_pham
        -- Logic: Chỉ trừ những lô có hạn nhỏ hơn ngày hôm nay VÀ chưa có trạng thái 4
        UPDATE sp
        SET sp.tong_so_luong = sp.tong_so_luong - lo.so_luong_con
        FROM tbl_san_pham sp
        INNER JOIN tbl_lo_thuoc lo ON lo.ma_sp = sp.id
        WHERE lo.han_su_dung < @NgayHienTai 
          AND lo.trang_thai_id != 4;

        -- BƯỚC 2: Cập nhật trạng thái lô thuốc thành 4 (Hết hạn)
        UPDATE tbl_lo_thuoc
        SET trang_thai_id = 4
        WHERE han_su_dung < @NgayHienTai 
          AND trang_thai_id != 4 AND trang_thai_id != 2;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
    END CATCH
END;
go

-------------------------------------------------------------------------------------------
-- 4. Index
-------------------------------------------------------------------------------------------

-------------------------------------------------------------------------------------------
-- 5. Auth
-------------------------------------------------------------------------------------------
-- Tạo permission
INSERT INTO tbl_permission (permission_key) VALUES 
('PRODUCT_VIEW'), ('PRODUCT_ADD'), ('PRODUCT_EDIT'), ('PRODUCT_EXPORT'), -- Quản lý sản phẩm
('SALE_ACCESS'), -- Bán hàng
('RETURN_ACCESS'), -- Trả hàng
('BATCH_VIEW'), ('BATCH_ADD'), ('BATCH_EDIT'), ('BATCH_EXPORT'), -- Quản lý lô
('INVOICE_VIEW'), ('INVOICE_RETURN_APPROVE'), ('INVOICE_EXPORT'), -- Quản lý hóa đơn & Duyệt trả hàng
('EMPLOYEE_MANAGE'), -- Quản lý nhân viên
('CUSTOMER_MANAGE'), ('CUSTOMER_EXPORT'), -- Quản lý khách hàng
('SEND_REPORT'),-- Gửi báo cáo 
('VOUCHER_MANAGE'), -- Quản lý voucher
('SUPPLIER_MANAGE'), -- Quản lý nhà cung cấp
('CONFIG_EMAIL') -- Cấu hình email
go

-- Tạo Role
INSERT INTO tbl_role (role_name) VALUES ('Manager'), ('Pharmacist');
go

-- Phân quyền cho Role (Mapping)
DECLARE @RoleManager INT = (SELECT id FROM tbl_role WHERE role_name = 'Manager');
DECLARE @RolePharmacist INT = (SELECT id FROM tbl_role WHERE role_name = 'Pharmacist');

-- Cấp quyền cho Quản lý (Manager)
INSERT INTO tbl_role_permission (role_id, permission_id)
SELECT @RoleManager, id FROM tbl_permission;

-- Cấp quyền cho Dược sĩ (Pharmacist)
INSERT INTO tbl_role_permission (role_id, permission_id)
SELECT @RolePharmacist, id FROM tbl_permission 
WHERE permission_key IN (
    'PRODUCT_VIEW', 
    'SALE_ACCESS', 
    'RETURN_ACCESS', 
    'BATCH_VIEW', 
    'INVOICE_VIEW',
    'CUSTOMER_MANAGE',
	'VOUCHER_MANAGE',
	'SUPPLIER_MANAGE',
	'SEND_REPORT'
);
go

-------------------------------------------------------------------------------------------
-- 5. Insert Data
-------------------------------------------------------------------------------------------
-- Insert trạng thái lô
INSERT INTO [tbl_trang_thai_lo] ([ten_trang_thai]) VALUES
(N'Đang lưu hành'),
(N'Đã hủy'),
(N'Đã bán hết'),
(N'Đã hết hạn')
GO

-- Insert nhà cung cấp
INSERT INTO tbl_nha_cung_cap 
([ma_nha_may], [ten_nha_cung_cap], [dia_chi], [so_dien_thoai], [ma_so_thue], [email], [website], [ghi_chu]) VALUES
('DHG', N'Công ty Cổ phần Dược Hậu Giang', N'288 Bis Nguyễn Văn Cừ, Quận Ninh Kiều, TP. Cần Thơ', '02923891433', '1800101990', 'contact@dhgpharma.com.vn', 'https://www.dhgpharma.com.vn', N'Nhà sản xuất và phân phối thuốc tân dược lớn tại Việt Nam'),
('TRA', N'Công ty Cổ phần Traphaco', N'75 Yên Ninh, Quận Ba Đình, Hà Nội', '02438232595', '0100108656', 'info@traphaco.com.vn', 'https://traphaco.com.vn', N'Chuyên sản xuất thuốc đông dược và thực phẩm chức năng'),
('OPC', N'Công ty Cổ phần Dược phẩm OPC', N'1017 Hồng Bàng, Quận 6, TP. Hồ Chí Minh', '02838553211', '0301428932', 'info@opcpharma.com', 'https://opcpharma.com', N'Chuyên cung cấp thuốc đông dược và dược liệu'),
('IMX', N'Công ty Cổ phần Dược phẩm Imexpharm', N'Số 4, Đường 30/4, TP. Cao Lãnh, Đồng Tháp', '02773851529', '1400102367', 'info@imexpharm.com', 'https://www.imexpharm.com', N'Nhà sản xuất thuốc đạt chuẩn GMP-WHO'),
('MKP', N'Công ty Cổ phần Hóa - Dược phẩm Mekophar', N'297/5 Lý Thường Kiệt, Quận 11, TP. Hồ Chí Minh', '02838663320', '0301452789', 'contact@mekophar.com.vn', 'https://www.mekophar.com.vn', N'Nhà cung cấp thuốc generic và thực phẩm chức năng'),
('PFR', N'Pfizer Inc.', N'235 East 42nd Street, New York, USA', '+1 212 733 2323', '02838553211', 'info@pfizer.com', 'https://www.pfizer.com', N'Tập đoàn dược phẩm toàn cầu của Mỹ, nổi tiếng với thuốc đặc trị và vắc xin'),
('SFI', N'Sanofi S.A.', N'54 Rue La Boétie, Paris, France', '+33 1 53 77 40 00', '02838553211', 'contact@sanofi.com', 'https://www.sanofi.com', N'Tập đoàn dược phẩm Pháp chuyên về thuốc kê toa và tiểu đường'),
('GLA', N'GlaxoSmithKline (GSK)', N'980 Great West Road, Brentford, London, UK', '+44 20 8047 5000', '02838553211', 'info@gsk.com', 'https://www.gsk.com', N'Nhà cung cấp thuốc, vắc xin và sản phẩm chăm sóc sức khỏe toàn cầu'),
('AST', N'AstraZeneca PLC', N'1 Francis Crick Avenue, Cambridge, UK', '+44 20 3749 5000', '02838553211', 'info@astrazeneca.com', 'https://www.astrazeneca.com', N'Chuyên thuốc đặc trị tim mạch, hô hấp và ung thư'),
('NOV', N'Novartis International AG', N'Lichtstrasse 35, Basel, Switzerland', '+41 61 324 1111', '02838553211', 'contact@novartis.com', 'https://www.novartis.com', N'Tập đoàn dược phẩm hàng đầu Thụy Sĩ với nhiều thương hiệu toàn cầu');
go

-- Insert đơn vị tính
INSERT INTO tbl_don_vi_tinh ([ten_dvt])
VALUES
(N'Viên'),
(N'Vỉ'),
(N'Hộp'),
(N'Lọ'),
(N'Chai'),
(N'Ống'),
(N'Gói'),
(N'Tuýp'),
(N'Hộp nhỏ'),
(N'Vỉ nhỏ');
go

-- Insert tiêu chuẩn chất lượng 
INSERT INTO tbl_tieu_chuan_chat_luong (ten_tieu_chuan) VALUES
(N'GMP-WHO'),
(N'GMP-EU'),
(N'GMP-Japan'),
(N'GMP-USA'),
(N'ISO 9001:2015'),
(N'TCCS'),
(N'Dược điển Việt Nam V'),
(N'USP'),
(N'BP'),
(N'EP');
go

-- Insert đường dùng
INSERT INTO tbl_duong_dung (ten_duong_dung) VALUES
(N'Đường uống'),
(N'Đường tiêm'),
(N'Đường hít'),
(N'Đường bôi ngoài da'),
(N'Đường đặt'),
(N'Đường ngậm'),
(N'Đường nhỏ mắt'),
(N'Đường nhỏ mũi'),
(N'Đường truyền tĩnh mạch');
go

-- Insert dạng bào chế
INSERT INTO tbl_dang_bao_che (ten_dang_bao_che) VALUES
(N'Viên nén'),
(N'Viên nang cứng'),
(N'Viên nang mềm'),
(N'Bột pha hỗn dịch'),
(N'Siro'),
(N'Dung dịch tiêm'),
(N'Hỗn dịch tiêm'),
(N'Dung dịch uống'),
(N'Mỡ bôi'),
(N'Gel'),
(N'Xịt họng'),
(N'Viên ngậm'),
(N'Viên sủi');
go

-- Insert loại sản phẩm 
INSERT INTO tbl_loai_san_pham ([ten_loai])
VALUES 
(N'Thuốc'),
(N'Thực phẩm chức năng'),
(N'Dụng cụ y tế');

-- Insert cửa hàng
insert into [dbo].[tbl_cua_hang]([ten_cua_hang], [dia_chi], [so_dang_ky], [giay_chung_nhan])
values (N'Cửa hàng 01 - Nhà thuốc Alami Pharmacy', N'12 Nguyễn Văn Bảo, phường Hạnh Thông, Thành phố Hồ Chí Minh', 'VS-(028) 38940390', '/images/giay-chung-nhan.png');
go

-- Insert hạng thành viên
INSERT INTO tbl_hang_thanh_vien(hang_thanh_vien, mo_ta, diem_toi_thieu)
VALUES
('Normal', N'Hạng mặc định cho khách mới', 0),
('Silver', N'Hạng bạc - điểm tích lũy từ 1000 điểm trở lên', 1000),
('Gold', N'Hạng vàng - điểm tích lũy từ 3000 điểm trở lên', 3000);
go

-- Insert nhân viên
insert into [dbo].[tbl_nhan_vien]([ho_ten], [so_dien_thoai], [email], [ngay_sinh], [gioi_tinh], [dia_chi], [avatar_url], [ngay_vao_lam], [cua_hang_id]) values
(N'Bùi Trung Kiên', '0363392352', 'buitrungkien2005qng@gmail.com', '2005-03-03', 1, N'Quảng Ngãi', '/images/empl/kien.png', '2020-01-01', 1),
(N'Lê Thị Kim Ngân', '0942897960', 'kimngan01062005@gmail.com', '2005-03-09', 0, N'Bình Thuận', '/images/empl/ngan.png', '2021-11-23', 1)
go

-- Insert role
insert into tbl_user_role (user_id, role_id) values 
('ALA010001', 1),
('ALA010002', 2)
go

-- Insert customer
INSERT INTO tbl_khach_hang ([ho_ten], [so_dien_thoai], [hang_thanh_vien], [diem_tich_luy])
VALUES
(N'Nguyễn Văn An', '0912345678', 1, 520),
(N'Trần Thị Bích Ngọc', '0987654321', 2, 1850),
(N'Lê Hoàng Minh', '0905123456', 1, 740),
(N'Phạm Thu Trang', '0938123123', 3, 4120),
(N'Đỗ Văn Hùng', '0977888999', 1, 610),
(N'Vũ Thị Hương', '0911999222', 2, 2750),
(N'Nguyễn Đức Long', '0903555666', 1, 890),
(N'Bùi Thị Mai', '0934666777', 3, 3580),
(N'Phan Thanh Tùng', '0966888555', 1, 460),
(N'Hoàng Thị Lan', '0977999000', 2, 2310),
(N'Đặng Minh Quân', '0912333444', 1, 980),
(N'Lý Thị Kim Chi', '0988444333', 2, 1630),
(N'Trịnh Văn Cường', '0909777666', 1, 720),
(N'Ngô Thị Nhung', '0935888999', 2, 2940),
(N'Tạ Hoàng Nam', '0977222111', 3, 5120),
(N'Đinh Thị Phương', '0912777888', 1, 340),
(N'Phùng Văn Thắng', '0909666555', 1, 910),
(N'Nguyễn Thị Thu Hà', '0966777444', 3, 3780),
(N'Lâm Đức Hiếu', '0934111222', 2, 2020),
(N'Trần Ngọc Anh', '0977333555', 3, 4590);
go

-- Insert voucher 
--insert into tbl_voucher ([gia_tri], [don_toi_thieu], [giam_toi_da], [ngay_bat_dau], [ngay_ket_thuc], [so_luot_su_dung_toi_da], [dieu_kien_hang_tv])
--values (15, 100000, 40000, '2025-11-1', '2025-11-20', 50, 2)


