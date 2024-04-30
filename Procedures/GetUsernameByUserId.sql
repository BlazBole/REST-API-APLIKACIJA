USE [UsersDb]
GO
/****** Object:  StoredProcedure [dbo].[GetUsernameByUserId]    Script Date: 30. 04. 2024 09:29:31 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[GetUsernameByUserId]
	@UserId INT
AS
BEGIN
	SET NOCOUNT ON;

	SELECT * FROM Users WHERE UserId = @UserId
END
