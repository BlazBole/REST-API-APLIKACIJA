USE [UsersDb]
GO
/****** Object:  StoredProcedure [dbo].[GetUserByEmail]    Script Date: 30. 04. 2024 09:27:59 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[GetUserByEmail]
	@Email nvarchar(50)
AS
BEGIN
	SET NOCOUNT ON;

	SELECT * FROM Users WHERE Email = @Email
END
