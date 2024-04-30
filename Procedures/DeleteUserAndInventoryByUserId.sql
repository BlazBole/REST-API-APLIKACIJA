USE [UsersDb]
GO
/****** Object:  StoredProcedure [dbo].[DeleteUserAndInventoryByUserId]    Script Date: 30. 04. 2024 09:13:35 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[DeleteUserAndInventoryByUserId] 
	@UserId INT
AS
BEGIN
	SET NOCOUNT ON;

    DELETE FROM Inventorys WHERE UserId = @UserId;
    DELETE FROM Users WHERE UserId = @UserId;
END
