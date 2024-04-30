USE [UsersDb]
GO
/****** Object:  StoredProcedure [dbo].[GetUserByUsername]    Script Date: 30. 04. 2024 09:28:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[GetUserByUsername]
	@username VARCHAR(255)
AS
BEGIN
	SET NOCOUNT ON;

	SELECT * FROM Users WHERE UserName = @Username
END
