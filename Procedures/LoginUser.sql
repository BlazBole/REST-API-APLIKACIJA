USE [UsersDb]
GO
/****** Object:  StoredProcedure [dbo].[LoginUser]    Script Date: 30. 04. 2024 09:30:38 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[LoginUser]
	@Email NVARCHAR(100),
    @Password NVARCHAR(100)
AS
BEGIN
	SET NOCOUNT ON;
	DECLARE @UserName NVARCHAR(100);

    SELECT @UserName = UserName
    FROM Users
    WHERE Email = @Email AND Password = @Password;

    SELECT @UserName AS UserName;
END
