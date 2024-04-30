USE [UsersDb]
GO
/****** Object:  StoredProcedure [dbo].[AddUser]    Script Date: 30. 04. 2024 09:12:14 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[AddUser]
	@UserName NVARCHAR(100),
    @Email NVARCHAR(100),
    @Password NVARCHAR(100),
    @Phone NVARCHAR(20),
    @Image NVARCHAR(MAX)
AS
BEGIN
	SET NOCOUNT ON;

    INSERT INTO Users (UserName, Email, Password, Phone, Image)
    VALUES (@UserName, @Email, @Password, @Phone, @Image);

    SELECT SCOPE_IDENTITY() AS UserId;
END
