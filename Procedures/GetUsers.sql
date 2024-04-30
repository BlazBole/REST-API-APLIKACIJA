USE [UsersDb]
GO
/****** Object:  StoredProcedure [dbo].[GetUsers]    Script Date: 30. 04. 2024 09:30:03 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[GetUsers]

AS
BEGIN
	SET NOCOUNT ON;

	SELECT * FROM Users
END
