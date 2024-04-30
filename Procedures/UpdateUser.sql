USE [UsersDb]
GO
/****** Object:  StoredProcedure [dbo].[UpdateUser]    Script Date: 30. 04. 2024 09:31:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[UpdateUser]
	@Id INT,
    @UserName NVARCHAR(255),
    @Phone NVARCHAR(20),
    @Image NVARCHAR(MAX)
AS
BEGIN
	SET NOCOUNT ON;

	UPDATE Users
    SET UserName = @UserName, Phone = @Phone, Image = @Image
    WHERE UserId = @Id;
END
