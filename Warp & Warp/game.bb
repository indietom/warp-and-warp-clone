Graphics 320, 240, 8, 2

SeedRnd MilliSecs()

Global frametimer=CreateTimer(60)
Global starttime=MilliSecs(),elapsedtime,fpscounter,curfps

Function collision(x, y, w, h, x2, y2, w2, h2)

	If y >= y2 + h2 Then Return False 
	If x >= x2 + w2 Then Return False 
	If y + h <= y2 Then Return False
	If x + w <= x2 Then Return False   
 Return True 
End Function 

Function lerp#(x#, y#, t#)
	Return t# * y# + (1-t#) * x#
End Function

Function distanceTo(x, y, x1, y2) 

	Local distnace
	distance = Sqr((x+x2)^2-(y+y2)^2)
	Return distance 

End Function

Function Frame(cell)
	Return cell * 16 + 1 + cell
End Function 

Function FrameSize(cell, size)
	Return cell * size + 1 + cell
End Function 

Global spritesheet = LoadImage("spritesheet.bmp")
MaskImage(spritesheet, 255, 0, 255)

Type player
	Field x
	Field y
	
	Field maxStepCount
	Field stepCount
	
	Field direction
	
	Field lives
	Field score
	
	Field imx
	Field imy
	
	Field dead
	
	Field fireRate
	
	Field currentFrame
End Type 

Type projectile
	Field x
	Field y
	
	Field stepCount
	Field maxStepCount
	
	Field imx
	Field imy
	
	Field direction 
	
	Field enemy
	
	Field destroy
End Type 

Type enemy
	Field x
	Field y
	
	Field direction
	
	Field typeOf
	
	Field hp
	
	Field stepCount
	Field maxStepCount
	
	Field turnCount
	Field maxTurnCount
	
	Field fireRate
	Field maxFireRate
	
	Field imx
	Field imy
	
	Field currentFrame 
	
	Field destroyCount
	
	Field destroy
End Type 

Type explosion
	Field x
	Field y
	
	Field dangerous
	
	Field lifeTime
	
	Field destroy
End Type

Type bomb
	Field x
	Field y
	
	Field lifeTime
	
	Field heightCanSpawn
	Field widthCanSpawn 
	
	Field destroy
End Type

Type tile
	Field x
	Field y
	
	Field destroy
End Type

Function addExpolsion(x2, y2, dangerous2)
	expolsion.explosion = New explosion
	expolsion\x = x2
	expolsion\y = y2
	
	expolsion\dangerous = dangerous2
End Function 

Function updateExplosion()
	For explosion.explosion = Each explosion
		If explosion\lifeTime >= 16 Then explosion\destroy = 1
		
		explosion\lifeTime = explosion\lifeTime + 1
		
		If explosion\destroy Then Delete explosion
	Next
End Function

Function drawExplosion()
	For explosion.explosion = Each explosion
		DrawImageRect(spritesheet, explosion\x, explosion\y, 69, 18, 16, 16)
	Next
End Function 

Function addBomb(x2, y2)
	bomb.bomb = New bomb
	bomb\x = x2
	bomb\y = y2
End Function 

Function updateBomb()
	For bomb.bomb = Each bomb
		bomb\lifeTime = bomb\lifeTime + 1
		If bomb\lifeTime >= 96 Then
			
			bomb\destroy = 1
		End If
		
		If bomb\destroy Then Delete bomb
	Next
End Function

Function drawBomb()
	For bomb.bomb = Each bomb
		DrawImageRect(spritesheet, bomb\x, bomb\y, 52, 69, 16, 16)
	Next
End Function 

Function addTile(x2, y2)
	tile.tile = New tile
	tile\x = x2
	tile\y = y2
End Function 

Function updateTile()
	For tile.tile = Each tile
		If tile\destroy Then Delete tile
	Next
End Function 

Function drawTile()
	For tile.tile = Each tile
		DrawImageRect(spritesheet, tile\x, tile\y, 86, 1, 16, 16)
	Next
End Function

Function addEnemy(x2, y2, typeOf2)
	enemy.enemy = New enemy
	enemy\x = x2
	enemy\y = y2
	
	enemy\hp = 2
	
	enemy\typeOf = typeOf2
	enemy\maxStepCount = 32
		
	enemy\maxTurnCount = 32
	
	enemy\maxFireRate = 64
	
	enemy\imx = 1
	enemy\imy = frame(enemy\typeOf+5)
End Function 

Function updateEnemy()
	For enemy.enemy = Each enemy 
		enemy\imx = frame(enemy\currentFrame)
	
		If enemy\stepCount >= enemy\maxStepCount Then
			If enemy\direction = 0 Then enemy\x = enemy\x + 16
			If enemy\direction = 1 Then enemy\x = enemy\x - 16
			If enemy\direction = 2 Then enemy\y = enemy\y - 16
			If enemy\direction = 3 Then enemy\y = enemy\y + 16

			enemy\currentFrame = enemy\currentFrame + 1
			
			enemy\stepCount = 0
		End If 
		
		If enemy\currentFrame >= 2 And enemy\hp >= 1 Then enemy\currentFrame = 0
		
		enemy\stepCount = enemy\stepCount + 1
		
		If enemy\hp >= 1 Then 
			For projectile.projectile = Each projectile
				If collision(projectile\x, projectile\y, 6, 6, enemy\x, enemy\y, 32, 32) Then
					enemy\hp = enemy\hp - 1
					projectile\destroy = 1
				End If
			Next
		End If 
		
		If enemy\hp <= 0 Then enemy\destroyCount = enemy\destroyCount + 1
		
		If enemy\destroyCount >= 1 Then
			enemy\stepCount = 0
			enemy\imx = 69
			enemy\imy = 18
			If enemy\destroyCount >= 16 Then enemy\destroy = 1
		End If 
		
		If enemy\destroy Then Delete enemy
	Next
End Function

Function drawEnemy()
	For enemy.enemy = Each enemy 
		DrawImageRect(spritesheet, enemy\x, enemy\y, enemy\imx, enemy\imy, 16, 16)
	Next
End Function 

Function addProjectile(x2, y2, direction2, enemy2)
	projectile.projectile = New projectile
	projectile\x = x2
	projectile\y = y2

	projectile\direction = direction2
	projectile\enemy = enemy2
	
	projectile\imx = 52
	
	projectile\maxStepCount = 8
	
	For player.player = Each player 
		projectile\stepCount = player\stepCount
	Next
End Function 

Function updateProjectile()
	For projectile.projectile = Each projectile
		
		projectile\imy = frameSize(projectile\direction, 6) 
		
		projectile\stepCount = projectile\stepCount + 1
		
		If projectile\x >= 320 Or projectile\x <= -6 Or projectile\y >= 240 Or projectile\y <= -6 Then projectile\destroy = 1
		For tile.tile = Each tile
			If collision(tile\x, tile\y, 16, 16, projectile\x, projectile\y, 6, 6) Then projectile\destroy = 1
		Next

		If projectile\stepCount >= projectile\maxStepCount Then
			If projectile\direction = 0 Then projectile\x = projectile\x + 16
			If projectile\direction = 1 Then projectile\x = projectile\x - 16
			If projectile\direction = 2 Then projectile\y = projectile\y - 16
			If projectile\direction = 3 Then projectile\y = projectile\y + 16
			
			projectile\stepCount = 0
		End If 
		
		If projectile\destroy Then Delete projectile
	Next
End Function 

Function drawProjectile()
	For projectile.projectile = Each projectile
		DrawImageRect(spritesheet, projectile\x, projectile\y, projectile\imx, projectile\imy, 6, 6)
	Next
End Function 

Function addPlayer()
	player.player = New player
	player\x = 160
	player\y = 16*5
	
	player\lives = 3
	
	player\maxStepCount = 12
	
	player\imx = 1
	player\imy = 1
End Function 

Function updatePlayer()
	For player.player = Each player
		If player\dead = 0 Then 
			player\imy = frame(player\direction)
			player\imx = frame(player\currentFrame)
			If player\currentFrame >= 2 Then player\currentFrame = 0
		End If 
		
		If KeyDown(203) Then
			player\stepCount = player\stepCount + 1
			player\direction = 1
		End If 
		If KeyDown(205) Then
			player\stepCount = player\stepCount + 1
			player\direction = 0
		End If 
		If KeyDown(200) Then
			player\stepCount = player\stepCount + 1
			player\direction = 2
		End If 
		If KeyDown(208) Then
			player\stepCount = player\stepCount + 1
			player\direction = 3
		End If 
		
		If KeyDown(205) = 0 And KeyDown(203) = 0 And KeyDown(200) = 0 And KeyDown(208) = 0 Then 
			player\stepCount = 0
		End If
			
		For tile.tile = Each tile
			If player\x + 16 = tile\x And player\y = tile\y And player\direction = 0 Then
				player\stepCount = 0
			End If
			If player\x - 16 = tile\x And player\y = tile\y And player\direction = 1 Then
				player\stepCount = 0
			End If
			If player\x = tile\x And player\y - 16  = tile\y And player\direction = 2 Then
				player\stepCount = 0
			End If
			If player\x = tile\x And player\y + 16  = tile\y And player\direction = 3 Then
				player\stepCount = 0
			End If
		Next
		
		If KeyHit(57) And player\fireRate <= 0 Then 
			addProjectile(player\x+8-3, player\y+8-3, player\direction, False)
			player\fireRate = 1
		End If 
		
		If KeyHit(29) And player\fireRate <= 0 Then 
			addBomb(player\x, player\y)
		End If 
		
		If player\fireRate >= 1 Then 
			player\fireRate = player\fireRate + 1
			If player\fireRate >= 32 Then player\fireRate = 0
		End If 
		
		If player\stepCount >= player\maxStepCount And player\dead = 0 Then
			If player\direction = 0 Then player\x = player\x + 16
			If player\direction = 1 Then player\x = player\x - 16
			If player\direction = 2 Then player\y = player\y - 16
			If player\direction = 3 Then player\y = player\y + 16
			
			player\currentFrame = player\currentFrame + 1
			
			player\stepCount = 0
		End If 
	Next
End Function 

Function drawPlayer()
	For player.player = Each player
		DrawImageRect(spritesheet, player\x, player\y, player\imx, player\imy, 16, 16)
	Next
End Function

Function update()
	updateProjectile()
	updateTile()
	updatePlayer()
	updateEnemy()
	updateBomb()
	updateExplosion()
End Function

Function draw()
	drawProjectile()
	drawBomb()
	drawTile()
	drawPlayer()
	drawEnemy()
	drawExplosion()
End Function 

addPlayer()
addEnemy(16*5, 16*5, 0)

For i = 0 To 10
	addTile(16*Rand(20), 16*Rand(15))
Next

While Not KeyHit(1)

	Cls 
		WaitTimer(frametimer)
		draw()
		update()
	Flip

Wend