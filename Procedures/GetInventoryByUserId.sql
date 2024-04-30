USE [UsersDb]
GO
/****** Object:  StoredProcedure [dbo].[GetInventoryByUserId]    Script Date: 30. 04. 2024 09:26:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[GetInventoryByUserId]
	@UserId INT
AS
BEGIN
	SET NOCOUNT ON;

	SELECT * FROM Inventorys WHERE UserId = @UserId
END
