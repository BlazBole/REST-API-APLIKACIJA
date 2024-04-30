USE [UsersDb]
GO
/****** Object:  StoredProcedure [dbo].[DeleteInventory]    Script Date: 30. 04. 2024 09:12:49 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

ALTER PROCEDURE [dbo].[DeleteInventory]
	@Id INT
AS
BEGIN
	SET NOCOUNT ON;

	DELETE FROM Inventorys WHERE Id = @Id;
END
