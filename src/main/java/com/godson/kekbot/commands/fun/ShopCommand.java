package com.godson.kekbot.commands.fun;

import com.darichey.discord.api.Command;
import com.darichey.discord.api.CommandCategory;
import com.godson.discoin4j.Discoin4J;
import com.godson.discoin4j.exceptions.*;
import com.godson.kekbot.CustomEmote;
import com.godson.kekbot.GSONUtils;
import com.godson.kekbot.KekBot;
import com.godson.kekbot.Profile.Background;
import com.godson.kekbot.Profile.Profile;
import com.godson.kekbot.Profile.Token;
import com.godson.kekbot.Questionaire.QuestionType;
import com.godson.kekbot.Questionaire.Questionnaire;
import com.godson.kekbot.Responses.Action;
import net.dv8tion.jda.core.MessageBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ShopCommand {
    public static Command shop = new Command("shop")
            .withCategory(CommandCategory.FUN)
            .withDescription("Opens up the item shop.")
            .withUsage("{p}shop <category>\n{p}shop <category> <page>\n{p}shop buy <category> <itemID>\n{p}shop info <category> <itemID>" + (GSONUtils.getConfig().getDcoinToken() == null ? "" : "\n{p}shop convert") + "\n\nAvailable Categories:\nTokens\nBackgrounds\n\n#Notes:\nArrows signify other pages in a shop.\nCrossed out items require you to be a higher level. You can find out what level is required by using {p}shop info <category> <itemID>.")
            .onExecuted(context -> {
                if (context.getArgs().length == 0) {
                    context.getTextChannel().sendMessage("Missing arguments, check " + KekBot.replacePrefix(context.getGuild(), "`{p}" + "help shop` to check all the arguments.")).queue();
                } else {
                    switch (context.getArgs()[0].toLowerCase()) {
                        case "token":
                        case "tokens":
                            List<Token> tokenShop = KekBot.tokenShop.getInventory();
                            int tokenShopPage;
                            if (context.getArgs().length < 2) tokenShopPage = 0;
                            else {
                                try {
                                    tokenShopPage = Integer.valueOf(context.getArgs()[1]) - 1;
                                } catch (NumberFormatException e) {
                                    context.getTextChannel().sendMessage(KekBot.respond(Action.NOT_A_NUMBER, "`" + context.getArgs()[1] + "`")).queue();
                                    break;
                                }
                            }
                            try {
                                if ((tokenShopPage * 9) >= tokenShop.size() || (tokenShopPage * 9) < 0) {
                                    context.getTextChannel().sendMessage("That page doesn't exist!").queue();
                                } else {
                                    context.getTextChannel().sendTyping().queue();
                                    context.getTextChannel().sendFile(drawTokenShop(Profile.getProfile(context.getAuthor()), tokenShop.subList(tokenShopPage * 9, ((tokenShopPage + 1) * 9 <= tokenShop.size() ? (tokenShopPage + 1) * 9 : tokenShop.size())), tokenShopPage > 0, (tokenShopPage + 1) * 9 < tokenShop.size(), tokenShopPage), "tokenshop.png", null).queue();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "background":
                        case "backgrounds":
                            List<Background> backgroundShop = KekBot.backgroundShop.getInventory();
                            int backgroundShopPage;
                            if (context.getArgs().length < 2) backgroundShopPage = 0;
                            else {
                                try {
                                    backgroundShopPage = Integer.valueOf(context.getArgs()[1]) - 1;
                                } catch (NumberFormatException e) {
                                    context.getTextChannel().sendMessage(KekBot.respond(Action.NOT_A_NUMBER, "`" + context.getArgs()[1] + "`")).queue();
                                    break;
                                }
                            }
                            try {
                                if ((backgroundShopPage * 6) >= backgroundShop.size() || (backgroundShopPage * 6) < 0) {
                                    context.getTextChannel().sendMessage("That page doesn't exist!").queue();
                                } else {
                                    context.getTextChannel().sendTyping().queue();
                                    context.getTextChannel().sendFile(drawBackgroundShop(Profile.getProfile(context.getAuthor()), backgroundShop.subList(backgroundShopPage * 6, ((backgroundShopPage + 1) * 6 <= backgroundShop.size() ? (backgroundShopPage + 1) * 6 : backgroundShop.size())), backgroundShopPage > 0, (backgroundShopPage + 1) * 6 < backgroundShop.size(), backgroundShopPage), "backgroundshop.png", null).queue();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "buy":
                            if (context.getArgs().length < 2)
                                context.getTextChannel().sendMessage(KekBot.replacePrefix(context.getGuild(), "Missing arguments, check `{p}help shop` to get more info.")).queue();
                            else {
                                switch (context.getArgs()[1]) {
                                    case "token":
                                    case "tokens":
                                        if (context.getArgs().length < 3)
                                            context.getTextChannel().sendMessage(KekBot.replacePrefix(context.getGuild(), "Missing arguments, check `{p}help shop` to get more info.")).queue();
                                        else {
                                            try {
                                                context.getTextChannel().sendMessage(KekBot.tokenShop.buy(KekBot.tokenShop.getInventory().get(Integer.valueOf(context.getArgs()[2]) - 1), context.getAuthor())).queue();
                                            } catch (NumberFormatException e) {
                                                context.getTextChannel().sendMessage(KekBot.respond(Action.NOT_A_NUMBER, "`" + context.getArgs()[2] + "`")).queue();
                                            }
                                        }
                                        break;
                                    case "background":
                                    case "backgrounds":
                                        if (context.getArgs().length < 3)
                                            context.getTextChannel().sendMessage(KekBot.replacePrefix(context.getGuild(), "Missing arguments, check `{p}help shop` to get more info.")).queue();
                                        else {
                                            try {
                                                context.getTextChannel().sendMessage(KekBot.backgroundShop.buy(KekBot.backgroundShop.getInventory().get(Integer.valueOf(context.getArgs()[2]) - 1), context.getAuthor())).queue();
                                            } catch (NumberFormatException e) {
                                                context.getTextChannel().sendMessage(KekBot.respond(Action.NOT_A_NUMBER, "`" + context.getArgs()[2] + "`")).queue();
                                            }
                                        }
                                        break;
                                    default:
                                        context.getTextChannel().sendMessage(KekBot.replacePrefix(context.getGuild(), "Invalid arguments, check `{p}help shop` to get more info.")).queue();
                                }
                            }
                            break;
                        case "info":
                            if (context.getArgs().length < 2)
                                context.getTextChannel().sendMessage(KekBot.replacePrefix(context.getGuild(), "Missing arguments, check `{p}help shop` to get more info.")).queue();
                            else {
                                switch (context.getArgs()[1].toLowerCase()) {
                                    case "token":
                                    case "tokens":
                                        if (context.getArgs().length < 3)
                                            context.getTextChannel().sendMessage(KekBot.replacePrefix(context.getGuild(), "Missing arguments, check `{p}help shop` to get more info.")).queue();
                                        else {
                                            try {
                                                Token selectedToken = KekBot.tokenShop.getInventory().get(Integer.valueOf(context.getArgs()[2]) - 1);
                                                String tokenInfo = "***Name:*** " + selectedToken.getName() +
                                                        "\n***Requires Level " + selectedToken.getRequiredLevel() + ".***" +
                                                        "\n***Description:*** " + selectedToken.getDescription() +
                                                        "\n***Preview:***";
                                                context.getTextChannel().sendTyping().queue();
                                                context.getTextChannel().sendFile(selectedToken.drawTokenImage(), "preview.png", new MessageBuilder().append(tokenInfo).build()).queue();
                                            } catch (NumberFormatException e) {
                                                context.getTextChannel().sendMessage(KekBot.respond(Action.NOT_A_NUMBER, "`" + context.getArgs()[2] + "`")).queue();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            } catch (IndexOutOfBoundsException e) {
                                                context.getTextChannel().sendMessage("There is no item in this shop with that ID.").queue();
                                            }
                                        }
                                        break;
                                    case "background":
                                    case "backgrounds":
                                        if (context.getArgs().length < 3)
                                            context.getTextChannel().sendMessage(KekBot.replacePrefix(context.getGuild(), "Missing arguments, check `{p}help shop` to get more info.")).queue();
                                        else {
                                            try {
                                                Background selectedBackground = KekBot.backgroundShop.getInventory().get(Integer.valueOf(context.getArgs()[2]) - 1);
                                                String backgroundInfo = "***Name:*** " + selectedBackground.getName() +
                                                        "\n***Requires Level " + selectedBackground.getRequiredLevel() + ".***" +
                                                        "\n***Description:*** " + selectedBackground.getDescription() +
                                                        "\n***Preview:***";
                                                context.getTextChannel().sendTyping().queue();
                                                context.getTextChannel().sendFile(selectedBackground.drawBackgroundImage(), "preview.png", new MessageBuilder().append(backgroundInfo).build()).queue();
                                            } catch (NumberFormatException e) {
                                                context.getTextChannel().sendMessage(KekBot.respond(Action.NOT_A_NUMBER, "`" + context.getArgs()[2] + "`")).queue();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            } catch (IndexOutOfBoundsException e) {
                                                context.getTextChannel().sendMessage("There is no item in this shop with that ID.").queue();
                                            }
                                        }
                                        break;
                                    default:
                                        context.getTextChannel().sendMessage(KekBot.replacePrefix(context.getGuild(), "Invalid arguments, check `{p}help shop` to get more info.")).queue();
                                }
                            }
                            break;
                        case "convert":
                            String url = "https://discoin.sidetrip.xyz/rates";
                            String unauthorized = "An error has occurred. This likely is because the bot owner screwed up somewhere...\n\nTranaction Canceled.";
                            if (GSONUtils.getConfig().getDcoinToken() != null) {
                                new Questionnaire(context)
                                        .addQuestion("Welcome to the Discoin Association's currency converter! You can convert all of your topkeks to currencies from other bots here!\n\nType the currency you want to convert to. (For the list of currencies, and their conversion rates, use the following link: " + url + ")\nYou can say `cancel` at any time to back out.", QuestionType.STRING)
                                        .execute(results -> {
                                            String to = results.getAnswer(0).toString();
                                            if (to.length() == 3) {
                                                new Questionnaire(results)
                                                        .addQuestion("How many topkeks do you want to convert?", QuestionType.INT)
                                                        .execute(results1 -> {
                                                            int amount = (int) results1.getAnswer(0);
                                                            Profile profile = Profile.getProfile(context.getAuthor());
                                                            if (!profile.canSpend(amount)) {
                                                                context.getTextChannel().sendMessage("You don't have that many topkeks.\n\nTransaction Canceled.").queue();
                                                                return;
                                                            }
                                                            try {
                                                                profile.spendTopKeks(amount);
                                                                profile.save();
                                                                Discoin4J.Confirmation confirmation = KekBot.discoin.makeTransaction(context.getAuthor().getId(), amount, to);
                                                                context.getTextChannel().sendMessage("Done! You should be receiving `" + confirmation.getResultAmount() + "` in the currency you selected shortly." +
                                                                        "\nYour reciept ID is: `" + confirmation.getReceiptCode() + "`." +
                                                                        "\nToday's remaining Discoin limit for currency `" + to + "`: " + confirmation.getLimitNow()).queue();
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            } catch (RejectedException e) {
                                                                switch (e.getStatus().getReason()) {
                                                                    case "verify required":
                                                                        context.getTextChannel().sendMessage("Hm, you're not verified on Discoin. You'll need to verify yourself before you can convert topkeks. You can do so here: " + url + "/verify\n\nTranaction Canceled.").queue();
                                                                        break;
                                                                    case "per-user limit exceeded":
                                                                        context.getTextChannel().sendMessage("You've already converted the maximum amount of coins for today! Try again tomorrow.\n\nTranaction Canceled.").queue();
                                                                        break;
                                                                    case "total limit exceeded":
                                                                        context.getTextChannel().sendMessage("Woah, this feature's been used so much, I've already transferred " + e.getStatus().getLimit() + " Discoins! I can't transfer anymore today! Check back tomorrow.\n\nTranaction Canceled.").queue();
                                                                        break;
                                                                    default:
                                                                        e.printStackTrace();
                                                                        break;
                                                                }
                                                            } catch (DiscoinErrorException e) {
                                                                context.getTextChannel().sendMessage("Hm, that doesn't seem like a valid currency.\n\nTranaction Canceled.").queue();
                                                            } catch (UnauthorizedException e) {
                                                                context.getTextChannel().sendMessage(unauthorized).queue();
                                                            } catch (UnknownErrorException e) {
                                                                context.getTextChannel().sendMessage("Yikes! I've found an error that shouldn't exist! Report this to the bot owner with the `ticket` command right away! `" + e.getMessage() + "`").queue();
                                                            }
                                                        });
                                            } else {
                                                context.getTextChannel().sendMessage("That's too " + (to.length() < 3 ? "short" : "long") + ", currency IDs are 3 characters long. Try again.").queue();
                                                results.reExecuteWithoutMessage();
                                            }
                                        });
                            }
                            break;
                    }
                }
            });

    private static byte[] drawTokenShop(Profile profile, List<Token> tokens, boolean prev, boolean next, int offset) throws IOException {
        BufferedImage shop3Shelf = ImageIO.read(new File("resources/shop/3shelf.png"));
        BufferedImage prevImg = ImageIO.read(new File("resources/shop/prev.png"));
        BufferedImage nextImg = ImageIO.read(new File(("resources/shop/next.png")));
        BufferedImage topkek = ImageIO.read(new File("resources/shop/topkek.png"));
        BufferedImage locked = ImageIO.read(new File("resources/shop/lockedToken.png"));
        Graphics2D graphics = shop3Shelf.createGraphics();
        if (prev) graphics.drawImage(prevImg, 247, 639, null);
        if (next) graphics.drawImage(nextImg, 339, 639, null);
        graphics.setColor(Color.white);
        graphics.setFont(new Font("Calibri", Font.BOLD, 16));
        for (int y = 0; y < Math.ceil((double) tokens.size() / 3d); y++) {
            for (int x = 0; x < (tokens.size() / (y + 1) < 3 ? tokens.size() - (y * 3) : 3); x++) {
                graphics.drawImage(tokens.get(x + (y * 3)).drawToken(), 70 + (125 * x), 226 + (130 * y), 80, 80, null);
                if (tokens.get(x + (y * 3)).getRequiredLevel() > profile.getLevel()) graphics.drawImage(locked, 70 + (125 * x), 226 + (130 * y),null);
                graphics.drawImage(topkek, 40 + (125 * x), 196 + (130 * y), null);
                graphics.drawString(String.valueOf(tokens.get(x + (y * 3)).getPrice()), 85 + (125 * x),215 + (130 * y));
                graphics.drawString(String.valueOf(((x + (y * 3)) + 1) + (offset * 9)), 53 + (125 * x), 257 + (130 * y));
            }
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(shop3Shelf, "png", outputStream);
        byte[] image = outputStream.toByteArray();
        outputStream.flush();
        outputStream.close();
        return image;
    }

    private static byte[] drawBackgroundShop(Profile profile, List<Background> backgrounds, boolean prev, boolean next, int offset) throws IOException {
        BufferedImage shop3Shelf = ImageIO.read(new File("resources/shop/3shelf.png"));
        BufferedImage prevImg = ImageIO.read(new File("resources/shop/prev.png"));
        BufferedImage nextImg = ImageIO.read(new File(("resources/shop/next.png")));
        BufferedImage topkek = ImageIO.read(new File("resources/shop/topkek.png"));
        BufferedImage locked = ImageIO.read(new File("resources/shop/lockedBackground.png"));
        Graphics2D graphics = shop3Shelf.createGraphics();
        if (prev) graphics.drawImage(prevImg, 247, 639, null);
        if (next) graphics.drawImage(nextImg, 339, 639, null);
        graphics.setColor(Color.white);
        graphics.setFont(new Font("Calibri", Font.BOLD, 16));
        for (int y = 0; y < Math.ceil((double) backgrounds.size() / 2d); y++) {
            for (int x = 0; x < (backgrounds.size() / (y + 1) < 2 ? backgrounds.size() - (y * 2) : 2); x++) {
                graphics.drawImage(backgrounds.get(x + (y * 2)).drawBackground(), 75 + (166 * x), 205 + (130 * y), 156, 94, null);
                if (backgrounds.get(x + (y * 2)).getRequiredLevel() > profile.getLevel()) graphics.drawImage(locked, 75 + (166 * x), 205 + (130 * y),null);
                graphics.drawImage(topkek, 4 + (393 * x), 199 + (130 * y), null);
                graphics.drawString(String.valueOf(backgrounds.get(x + (y * 2)).getPrice()), 40 + (393 * x),220 + (130 * y));
                graphics.drawString(String.valueOf(((x + (y * 2)) + 1) + (offset * 6) ), 32 + (393 * x), 252 + (130 * y));
            }
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(shop3Shelf, "png", outputStream);
        byte[] image = outputStream.toByteArray();
        outputStream.flush();
        outputStream.close();
        return image;
    }
}
