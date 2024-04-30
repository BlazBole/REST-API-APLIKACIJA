USE [UsersDb]
GO
/****** Object:  StoredProcedure [dbo].[GetUser]    Script Date: 30. 04. 2024 09:27:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[GetUser]
	@Id INT
AS
BEGIN
	SET NOCOUNT ON;

	SELECT * FROM Users WHERE UserId = @Id
END
