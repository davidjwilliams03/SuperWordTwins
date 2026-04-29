import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JPanel;

public class Batman {
    private JPanel panel;

    private int x;
    private int y;
    private int width;
    private int height;
    private int dy;
    private int dx;

    Animation idleAnim, runL, runR, jump, fall, recover, attack1, attack2, currAnim;
    private BufferedImage idleSheet, runLSheet, runRSheet, jumpSheet, fallSheet, recoverSheet, attack1Sheet, attack2Sheet;
    private BufferedImage frame;

    private boolean isMoving;

    private Random random;

    public Batman(JPanel panel){
        this.panel = panel;
        x = 10;
        y = 200;
        width = 80;
        height = 120;
        dy = 10;
        dx = 10;
        isMoving = true;
        this.idleAnim = new Animation(true);
        this.runL = new Animation(true);
        this.runR = new Animation(true);
        this.jump = new Animation(false);
        this.fall = new Animation(false);
        this.recover = new Animation(false);
        this.attack1 = new Animation(false);
        this.attack2 = new Animation(false);
        
        loadAnimations();
        this.currAnim = idleAnim;
        this.currAnim.start();

        random = new Random();
    }

    public void loadAnimations(){//load all animations
        //idle 
        idleSheet = ImageManager.loadBufferedImage("images/characters/batmanIdle.png");
        for(int i = 0; i < 4; i++){
            frame = idleSheet.getSubimage(i*62, 0, 62, 90);
            idleAnim.addFrame(frame, 150);
        }

        //run left
        runLSheet = ImageManager.loadBufferedImage("images/characters/batmanWalkL.png");
        for(int i = 0; i < 5; i++){
            frame = runLSheet.getSubimage(i*71, 0, 71, 92);
            runL.addFrame(frame, 110);
        }
        for(int i = 3; i > 0 ; i--){
            frame = runLSheet.getSubimage(i*71, 0, 71, 92);
            runL.addFrame(frame, 110);
        }
        
        //run right
        runRSheet = ImageManager.loadBufferedImage("images/characters/batmanWalkR.png");
        frame = ImageManager.loadBufferedImage("images/characters/r1.png");
        runR.addFrame(frame, 110);
        frame = ImageManager.loadBufferedImage("images/characters/r2.png");
        runR.addFrame(frame, 110);
        frame = ImageManager.loadBufferedImage("images/characters/r3.png");
        runR.addFrame(frame, 110);
        frame = ImageManager.loadBufferedImage("images/characters/r4.png");
        runR.addFrame(frame, 110);
        frame = ImageManager.loadBufferedImage("images/characters/r5.png");
        runR.addFrame(frame, 110);
        frame = ImageManager.loadBufferedImage("images/characters/r4.png");
        runR.addFrame(frame, 110);
        frame = ImageManager.loadBufferedImage("images/characters/r3.png");
        runR.addFrame(frame, 110);
        frame = ImageManager.loadBufferedImage("images/characters/r2.png");
        runR.addFrame(frame, 110);

        //jump
        jumpSheet = ImageManager.loadBufferedImage("images/characters/batmanJump.png");
        frame = ImageManager.loadBufferedImage("images/characters/j1.png");
        jump.addFrame(frame, 200);
        frame = ImageManager.loadBufferedImage("images/characters/j2.png");
        jump.addFrame(frame, 200); 
        frame = ImageManager.loadBufferedImage("images/characters/j3.png");
        jump.addFrame(frame, 300);   
        frame = ImageManager.loadBufferedImage("images/characters/j4.png");
        jump.addFrame(frame, 110);    

        //fall
        fallSheet = ImageManager.loadBufferedImage("images/characters/batmanFall.png");
        frame = ImageManager.loadBufferedImage("images/characters/fall1.png");
        fall.addFrame(frame, 500);
        frame = ImageManager.loadBufferedImage("images/characters/fall2.png");
        fall.addFrame(frame, 500);
        frame = ImageManager.loadBufferedImage("images/characters/fall3.png");
        fall.addFrame(frame, 500);
        frame = ImageManager.loadBufferedImage("images/characters/fall4.png");
        fall.addFrame(frame, 500);

        //recover after fall
        recoverSheet = ImageManager.loadBufferedImage("images/characters/batmanRecover.png");
        frame = ImageManager.loadBufferedImage("images/characters/recover1.png");
        recover.addFrame(frame, 250);
        frame = ImageManager.loadBufferedImage("images/characters/recover2.png");
        recover.addFrame(frame, 250);
        frame = ImageManager.loadBufferedImage("images/characters/recover3.png");
        recover.addFrame(frame, 250);
        
        //attack 1
        attack1Sheet = ImageManager.loadBufferedImage("images/characters/batmanAttack1.png");
        frame = ImageManager.loadBufferedImage("images/characters/a1.1.png");
        attack1.addFrame(frame, 100);
        frame = ImageManager.loadBufferedImage("images/characters/a1.2.png");
        attack1.addFrame(frame, 100);
        frame = ImageManager.loadBufferedImage("images/characters/a1.3.png");
        attack1.addFrame(frame, 200);
        frame = ImageManager.loadBufferedImage("images/characters/a1.2.png");
        attack1.addFrame(frame, 100);

        //attack 2
        attack2Sheet = ImageManager.loadBufferedImage("images/characters/batmanAttack2.png");
        for(int i = 0; i < 4; i++){
            frame = attack2Sheet.getSubimage(i*81, 0, 81, 84);
            attack2.addFrame(frame, 100);
        }
        for(int i = 2; i >= 0 ; i--){
            frame = attack2Sheet.getSubimage(i*81, 0, 81, 84);
            attack2.addFrame(frame, 100);
        }

    }

    public void draw(Graphics2D g2){
        if(!isMoving)
            g2.drawImage(idleAnim.getImage(), x, y, null);
        else {
            g2.drawImage(currAnim.getImage(), x, y, null);
        }
    }

    public void setIdle(){
        if(currAnim!=idleAnim){
                idleAnim.start();
                currAnim = idleAnim;
        }
    }

    public Animation getCurrAnim(){
        return currAnim;
    }

    /*public String getAnimName(){
        if(currAnim == idleAnim){
            return "idle";
        }
        else {
            if(currAnim == runL){
            return "runL";
            }
            else {
                if(currAnim == runR){
                    return "runR";
                }
                else{
                    if(currAnim == jump){
                        return "jump";
                    }
                    else {
                        if(currAnim == attack1 || currAnim == attack2){
                            return "attack";
                        }
                    }
                }
            }
        }
        return "";
    }*/

    public void move(int direction){
        if(!panel.isVisible()){
            return;
        }

        if(direction == 1){
            if(currAnim!=runL){
                runL.start();
                currAnim = runL;
            }
            x = x-dx;
            if(x < 0)
                x = 0;  
        }
        else {
            if(direction == 2){
                if(currAnim!=runR){
                runR.start();
                currAnim = runR;
                }
                x = x+dx;
                if(x > panel.getWidth() - width)
                    x = panel.getWidth() - width;  
            }
            else{
                if(direction == 3){
                    if(currAnim != jump){
                        jump.start();
                        currAnim = jump;
                    }
                }
                else {
                    if(direction == 4){
                        if (currAnim != attack1 && currAnim != attack2) {
                            int n = random.nextInt(2);
                            if(n == 0)
                                currAnim = attack1;
                            else
                                currAnim = attack2;
                            currAnim.start();
                            
                        }
                    }
                }

            }
        }
    }

    public void update(){
        if(!panel.isVisible()){
            return;
        } 
        if(!currAnim.isStillActive()){
            setIdle();
        }
            
        currAnim.update();
    }    
}